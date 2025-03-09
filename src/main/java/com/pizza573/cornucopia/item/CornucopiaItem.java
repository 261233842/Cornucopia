package com.pizza573.cornucopia.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// todo 先实现消耗食物，再实现对应的消耗逻辑
public class CornucopiaItem extends Item
{
    // static 变量，所有对象共享同一个静态字段值，示例：计数器、配置常量等。
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
    private static final int ITEM_NAME_COLOR = Mth.color(0.133f, 0.545f, 0.133f);// 暗绿色
    private static final int MAX_WEIGHT = 128; // 1.20.1 不做附魔
    private static final int REDUCE_TIME = 6; // 0.3s * 20tick/s = 6tick
//    private int suitableFoodIndex;
//    private ItemStack suitableFood = ItemStack.EMPTY;

    public CornucopiaItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack)
    {
        return Component.translatable(super.getName(stack).getString()).withStyle(style -> style.withColor(ITEM_NAME_COLOR));
    }

    // 供物品属性weight使用，类似boson的magicIngot，“使物品能够动态的切换贴图”
    public static float getWeightDisplay(ItemStack stack)
    {
        return (float) getContentWeight(stack) / MAX_WEIGHT;
    }

    // 存储相关基本逻辑 ***start***
    private static int add(ItemStack pBundleStack, ItemStack pInsertedStack)
    {
        if (!pInsertedStack.isEmpty() && pInsertedStack.getItem().canFitInsideContainerItems()) {
            CompoundTag compoundtag = pBundleStack.getOrCreateTag();
            if (!compoundtag.contains("Items")) {
                compoundtag.put("Items", new ListTag());
            }

            int i = getContentWeight(pBundleStack);// 获取容器内物品总权重
            int j = getWeight(pInsertedStack);// 获取插入单个物品的权重
            int k = Math.min(pInsertedStack.getCount(), (MAX_WEIGHT - i) / j);// 此次插入物品的数量
            if (k == 0) {
                return 0;
            } else {
                ListTag listtag = compoundtag.getList("Items", Tag.TAG_COMPOUND);
                Optional<CompoundTag> optional = getMatchingItem(pInsertedStack, listtag);// 寻找拥有相同tag的Item（匹配的itemStack）
                if (optional.isPresent()) {// 找到匹配的物品
                    CompoundTag compoundTag1 = optional.get();// compoundTag1
                    ItemStack itemstack = ItemStack.of(compoundTag1);// 匹配的itemStack
                    if (k + itemstack.getCount() > 64) {
                        int increment = 64 - itemstack.getCount();
                        int rest = k - increment;
                        // grow
                        itemstack.grow(increment);
                        itemstack.save(compoundTag1);
                        listtag.remove(compoundTag1);
                        listtag.add(0, compoundTag1);
                        // new CompoundTag
                        ItemStack itemstack1 = pInsertedStack.copyWithCount(rest);
                        CompoundTag compoundTag2 = new CompoundTag();
                        itemstack1.save(compoundTag2);
                        listtag.add(0, compoundTag2);
                    } else if (k + itemstack.getCount() <= 64) {
                        itemstack.grow(k);
                        itemstack.save(compoundTag1);
                        listtag.remove(compoundTag1);
                        listtag.add(0, compoundTag1);
                    }
                } else {// 未找到匹配的物品
                    ItemStack itemstack1 = pInsertedStack.copyWithCount(k);
                    CompoundTag compoundTag2 = new CompoundTag();
                    itemstack1.save(compoundTag2);
                    listtag.add(0, compoundTag2);
                }

                return k;
            }
        } else {
            return 0;
        }
    }

    private static Optional<CompoundTag> getMatchingItem(ItemStack pStack, ListTag pList)
    {
        return pList.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter((compoundTag) -> {
            return ItemStack.isSameItemSameTags(ItemStack.of(compoundTag), pStack);
        }).findFirst();
    }

    private static int getWeight(ItemStack pStack)
    {
        if (pStack.is(Items.BUNDLE)) {
            return 4 + getContentWeight(pStack);
        } else {
            if ((pStack.is(Items.BEEHIVE) || pStack.is(Items.BEE_NEST)) && pStack.hasTag()) {
                CompoundTag compoundtag = BlockItem.getBlockEntityData(pStack);
                if (compoundtag != null && !compoundtag.getList("Bees", Tag.TAG_COMPOUND).isEmpty()) {
                    return 64;
                }
            }

            return 64 / pStack.getMaxStackSize();
        }
    }

    private static int getContentWeight(ItemStack pStack)
    {
        return getContents(pStack).mapToInt((itemStack) -> getWeight(itemStack) * itemStack.getCount()).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack pStack)
    {
        CompoundTag compoundtag = pStack.getOrCreateTag();
        if (!compoundtag.contains("Items")) {
            return Optional.empty();
        } else {
            ListTag listtag = compoundtag.getList("Items", Tag.TAG_COMPOUND);
            if (listtag.isEmpty()) {
                return Optional.empty();
            } else {
                int i = 0;
                CompoundTag compoundtag1 = listtag.getCompound(0);
                ItemStack itemstack = ItemStack.of(compoundtag1);
                listtag.remove(0);
                if (listtag.isEmpty()) {
                    pStack.removeTagKey("Items");
                }

                return Optional.of(itemstack);
            }
        }
    }

    private static Stream<ItemStack> getContents(ItemStack pStack)
    {
        CompoundTag compoundtag = pStack.getTag();
        if (compoundtag == null) {
            return Stream.empty();
        } else {
            ListTag listtag = compoundtag.getList("Items", Tag.TAG_COMPOUND);
            return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }
    // 存储相关基本逻辑 ***end***

    public boolean overrideStackedOnOther(ItemStack pStack, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer)
    {
        if (pStack.getCount() != 1 || pAction != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack itemstack = pSlot.getItem();
            if (itemstack.isEmpty()) {
                this.playRemoveOneSound(pPlayer);
                removeOne(pStack).ifPresent((itemStack) -> {
                    add(pStack, pSlot.safeInsert(itemStack));
                });
            } else if (itemstack.getItem().canFitInsideContainerItems() && itemstack.getFoodProperties(pPlayer) != null) {
                int i = (MAX_WEIGHT - getContentWeight(pStack)) / getWeight(itemstack);
                int j = add(pStack, pSlot.safeTake(itemstack.getCount(), i, pPlayer));
                if (j > 0) {
                    this.playInsertSound(pPlayer);
                }
            }

            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack pStack, @NotNull ItemStack pOther, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer, @NotNull SlotAccess pAccess)
    {
        if (pStack.getCount() != 1) return false;
        if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer)) {
            if (pOther.isEmpty()) {
                removeOne(pStack).ifPresent((itemStack) -> {
                    this.playRemoveOneSound(pPlayer);
                    pAccess.set(itemStack);
                });
            } else if (pOther.getFoodProperties(pPlayer) != null) {
                int i = add(pStack, pOther);
                if (i > 0) {
                    this.playInsertSound(pPlayer);
                    pOther.shrink(i);
                }
            }

            return true;
        } else {
            return false;
        }
    }
/*

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (itemstack.isEdible()) {
            if (pPlayer.canEat(itemstack.getFoodProperties(pPlayer).canAlwaysEat())) {
                pPlayer.startUsingItem(pUsedHand);
                return InteractionResultHolder.consume(itemstack);
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        } else {
            return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
        }
    }
*/

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity)
    {
        // 消耗食物

        return stack;
    }
/*

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack)
    {
//        return suitableFood.getItem().getUseAnimation(suitableFood);
    }

    // 获取使用时间
    @Override
    public int getUseDuration(@NotNull ItemStack cornucopia)
    {
//        FoodProperties foodProperties = suitableFood.getFoodProperties(entity);
        // 一般食物的使用时间为1.6s，海带0.8s
//        return foodProperties != null ? foodProperties.eatDurationTicks() - REDUCE_TIME : 0;

    }
*/

    public void appendHoverText(@NotNull ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add(Component.translatable("item.minecraft.bundle.fullness", getContentWeight(pStack), 128).withStyle(ChatFormatting.GRAY));
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("item.minecraft.cornucopia.description").withStyle(style -> style.withColor(ITEM_NAME_COLOR)));
        }
    }

    public boolean isBarVisible(@NotNull ItemStack pStack)
    {
        return getContentWeight(pStack) > 0;
    }

    public int getBarWidth(@NotNull ItemStack pStack)
    {
        return Math.min(1 + 12 * getContentWeight(pStack) / MAX_WEIGHT, 13);
    }

    public int getBarColor(@NotNull ItemStack pStack)
    {
        return BAR_COLOR;
    }

    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack)
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        getContents(pStack).forEach(nonnulllist::add);
        return Optional.of(new BundleTooltip(nonnulllist, getContentWeight(pStack)));
    }


    // 不能被放入背包之类的容器物品，不影响部分容器实体，如：chest
    public boolean canFitInsideContainerItems()
    {
        return false;
    }

    private void playRemoveOneSound(Entity pEntity)
    {
        pEntity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity pEntity)
    {
        pEntity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }
}
