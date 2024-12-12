package com.pizza573.cornucopia.item.components;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public final class CornucopiaContents implements TooltipComponent
{
    public static final CornucopiaContents EMPTY = new CornucopiaContents(List.of());
    // todo 作用
    private static final int NO_STACK_INDEX = -1;
    // The codec to read/write the data to disk
    public static final Codec<CornucopiaContents> CODEC;
    // The codec to read/write the data across the network
    public static final StreamCodec<RegistryFriendlyByteBuf, CornucopiaContents> STREAM_CODEC;
    // 用于物品存储的相关数据
    final List<ItemStack> items;
    final Fraction weight;
    final int maxSize;

    // 内部使用
    CornucopiaContents(List<ItemStack> items, Fraction weight, int maxSize)
    {
        this.items = items;
        this.weight = weight;
        this.maxSize = maxSize;
    }

    // 供外部使用（大部分情况）
    public CornucopiaContents(List<ItemStack> items)
    {
        // 默认容量 128
        this(items, computeContentWeight(items), 128);
    }

    // 计算存储物品的重量
    private static Fraction computeContentWeight(List<ItemStack> items)
    {
        Fraction fraction = Fraction.ZERO;

        ItemStack itemstack;
        for (Iterator<ItemStack> var2 = items.iterator(); var2.hasNext(); fraction = fraction.add(getWeight(itemstack).multiplyBy(Fraction.getFraction(itemstack.getCount(), 1)))) {
            itemstack = var2.next();
        }

        return fraction;
    }

    // 原版加了个蜂箱检测
    // 获取权重
    static Fraction getWeight(ItemStack stack)
    {
        // 修改容量权重逻辑
        return Fraction.getFraction(1, stack.getMaxStackSize());
    }

    public ItemStack getItemUnsafe(int index)
    {
        return this.items.get(index);
    }

    public Stream<ItemStack> itemCopyStream()
    {
        return this.items.stream().map(ItemStack::copy);
    }

    public Iterable<ItemStack> items()
    {
        return this.items;
    }

    public Iterable<ItemStack> itemsCopy()
    {
        return Lists.transform(this.items, ItemStack::copy);
    }

    public int size()
    {
        return this.items.size();
    }

    public Fraction weight()
    {
        return this.weight;
    }

    public boolean isEmpty()
    {
        return this.items.isEmpty();
    }

    // Contents需要重写equals方法
    @Override
    public boolean equals(Object other)
    {
        // ==比较两个对象的地址
        if (this == other) {
            return true;
        } else {
            // 地址不同，试着比较weight和items的内容是否完全相等
            return other instanceof CornucopiaContents cornucopiaContents
                    && this.weight.equals(cornucopiaContents.weight)
                    && ItemStack.listMatches(this.items, cornucopiaContents.items);
        }
    }

    // Contents需要重写hashCode方法
    @Override
    public int hashCode()
    {
        return ItemStack.hashStackList(this.items);
    }

    @Override
    public String toString()
    {
        return "CornucopiaContents" + this.items;
    }

    private List<ItemStack> getContentCopy()
    {
        return this.items.stream().map(ItemStack::copy).toList();
    }

    static {
        // todo 增加range
        // 在类加载时，初始化 CODEC 和 STREAM_CODEC；模组注册 DataComponents 与原版注册不同
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.listOf().fieldOf("items").forGetter(CornucopiaContents::getContentCopy)
        ).apply(instance, CornucopiaContents::new));
        STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list(1024)), CornucopiaContents::getContentCopy,
                CornucopiaContents::new
        );
    }

    // 由于CornucopiaContents的属性是final的，所以需要一个可变类来修改（此处为内部类）
    public static class Mutable
    {
        private final List<ItemStack> items;
        // weight 总共的占比权重情况
        private Fraction weight;
        // todo 兼容”容量附魔“
        private int maxSize;

        public Mutable(CornucopiaContents contents)
        {
            this.items = new ArrayList<>(contents.items);
            this.weight = contents.weight;
            this.maxSize = contents.maxSize;
        }

        public CornucopiaContents.Mutable clearItems()
        {
            this.items.clear();
            this.weight = Fraction.ZERO;
            return this;
        }

        private int findStackableIndex(ItemStack stack)
        {
            if (!stack.isStackable()) {
                return -1;
            } else {
                for (int i = 0; i < this.items.size(); ++i) {
                    if (ItemStack.isSameItemSameComponents(this.items.get(i), stack) && this.items.get(i).getCount() < 64) {
                        return i;
                    }
                }

                return -1;
            }
        }

        private int getMaxAmountToAdd(ItemStack stack)
        {
            int itemValues = Mth.mulAndTruncate(CornucopiaContents.getWeight(stack), 64);
            int usedSpace = Mth.mulAndTruncate(this.weight, 64);
            int freeSpace = this.maxSize - usedSpace;
            return Math.max(freeSpace / itemValues, 0);
        }

        public void setMaxSize(int maxSize)
        {
            if (maxSize < 64)
                return;
            this.maxSize = maxSize;
        }

        public int tryInsert(ItemStack stack)
        {
            if (!stack.isEmpty() && stack.getItem().canFitInsideContainerItems()) {
                // i 是可堆叠数量
                int i = Math.min(stack.getCount(), this.getMaxAmountToAdd(stack));
                if (i == 0) {
                    return 0;
                } else {
                    // todo 优化
                    this.weight = weight.add(CornucopiaContents.getWeight(stack).multiplyBy(Fraction.getFraction(i, 1)));
                    // 寻找可以堆叠的stackIndex
                    int j = this.findStackableIndex(stack);
                    // 如果找到可堆叠的 ItemStack
                    if (j != -1) {
                        // cornucopia 中寻找到可以堆叠的 ItemStack
                        ItemStack stackableItemStack = this.items.remove(j);
                        //
                        if (stackableItemStack.getCount() + i > stackableItemStack.getMaxStackSize()) {
                            // 64 stackableItemStack(60) i(5) restCount(1) needCount(4)
                            // restCount 不可能大于 maxStackSize
                            int restCount = (stackableItemStack.getCount() + i) - stackableItemStack.getMaxStackSize();
                            int needCount = stackableItemStack.getMaxStackSize() - stackableItemStack.getCount();

                            ItemStack stackedItemStack = stackableItemStack.copyWithCount(stackableItemStack.getMaxStackSize());
                            stack.shrink(needCount);
                            this.items.addFirst(stackedItemStack);
                            this.items.addFirst(stack.split(restCount));
                        } else {
                            ItemStack stackedItemStack = stackableItemStack.copyWithCount(stackableItemStack.getCount() + i);
                            stack.shrink(i);
                            this.items.addFirst(stackedItemStack);
                        }
                    } else {// 没有找到可堆叠的 ItemStack
                        // stack.split(i)：把 Stack 的数量减少 i，并返回另一个 数量为 i 的 ItemStack
                        this.items.addFirst(stack.split(i));
                    }

                    return i;
                }
            } else {
                return 0;
            }
        }

        public int tryTransfer(Slot slot, Player player)
        {
            ItemStack itemstack = slot.getItem();
            // i 可最大插入数量
            int i = this.getMaxAmountToAdd(itemstack);
            return this.tryInsert(slot.safeTake(itemstack.getCount(), i, player));
        }

        @Nullable
        public ItemStack removeOne()
        {
            if (this.items.isEmpty()) {
                return null;
            } else {
                // 为什么要加 copy() ？ --> 原始item删除了，创建个副本
                ItemStack itemstack = this.items.removeFirst().copy();
                this.weight = this.weight.subtract(CornucopiaContents.getWeight(itemstack).multiplyBy(Fraction.getFraction(itemstack.getCount(), 1)));
                return itemstack;
            }
        }

        @Nullable
        public ItemStack removeOne(int subCount)
        {
            if (this.items.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                ItemStack itemstack = this.items.getFirst();
                ItemStack resultItem = this.items.getFirst().copy();
                int count = itemstack.getCount();

                if (Math.max(count - subCount, 0) == 0) {
                    return this.removeOne();
                } else {
                    itemstack.setCount(count - subCount);
                    resultItem.setCount(subCount);
                    this.weight = this.weight.subtract(CornucopiaContents.getWeight(itemstack).multiplyBy(Fraction.getFraction(subCount, 1)));
                    return resultItem;
                }
            }
        }

        @Nullable
        public ItemStack getOne()
        {
            if (this.items.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                return this.items.getFirst().copy();
            }
        }

        public Fraction weight()
        {
            return this.weight;
        }

        public CornucopiaContents toImmutable()
        {
            return new CornucopiaContents(List.copyOf(this.items), this.weight, this.maxSize);
        }
    }
}
