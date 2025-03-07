package com.pizza573.cornucopia.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// todo 【实现存储功能】
@Mixin(InstrumentItem.class)
public class InstrumentItemMixin extends Item
{
    //    private static final String TAG_ITEMS = "Items";
/*    @Unique
    private static final int MAX_WEIGHT = 64;
    @Unique
    private static final int BUNDLE_IN_BUNDLE_WEIGHT = 4;
    @Unique
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);*/

    public InstrumentItemMixin(Item.Properties pProperties)
    {
        super(pProperties);
    }
/*

    // 存储相关基本逻辑 start
    private static int add(ItemStack pBundleStack, ItemStack pInsertedStack)
    {
        if (!pInsertedStack.isEmpty() && pInsertedStack.getItem().canFitInsideContainerItems()) {
            CompoundTag compoundtag = pBundleStack.getOrCreateTag();
            if (!compoundtag.contains("Items")) {
                compoundtag.put("Items", new ListTag());
            }

            int i = getContentWeight(pBundleStack);
            int j = getWeight(pInsertedStack);
            int k = Math.min(pInsertedStack.getCount(), (64 - i) / j);
            if (k == 0) {
                return 0;
            } else {
                ListTag listtag = compoundtag.getList("Items", 10);
                Optional<CompoundTag> optional = getMatchingItem(pInsertedStack, listtag);
                if (optional.isPresent()) {
                    CompoundTag compoundtag1 = optional.get();
                    ItemStack itemstack = ItemStack.of(compoundtag1);
                    itemstack.grow(k);
                    itemstack.save(compoundtag1);
                    listtag.remove(compoundtag1);
                    listtag.add(0, (Tag) compoundtag1);
                } else {
                    ItemStack itemstack1 = pInsertedStack.copyWithCount(k);
                    CompoundTag compoundtag2 = new CompoundTag();
                    itemstack1.save(compoundtag2);
                    listtag.add(0, (Tag) compoundtag2);
                }

                return k;
            }
        } else {
            return 0;
        }
    }

    private static Optional<CompoundTag> getMatchingItem(ItemStack pStack, ListTag pList)
    {
        return pStack.is(Items.BUNDLE) ? Optional.empty() : pList.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter((p_186350_) -> {
            return ItemStack.isSameItemSameTags(ItemStack.of(p_186350_), pStack);
        }).findFirst();
    }

    private static int getWeight(ItemStack pStack)
    {
        if (pStack.is(Items.BUNDLE)) {
            return 4 + getContentWeight(pStack);
        } else {
            if ((pStack.is(Items.BEEHIVE) || pStack.is(Items.BEE_NEST)) && pStack.hasTag()) {
                CompoundTag compoundtag = BlockItem.getBlockEntityData(pStack);
                if (compoundtag != null && !compoundtag.getList("Bees", 10).isEmpty()) {
                    return 64;
                }
            }

            return 64 / pStack.getMaxStackSize();
        }
    }

    private static int getContentWeight(ItemStack pStack)
    {
        return getContents(pStack).mapToInt((p_186356_) -> {
            return getWeight(p_186356_) * p_186356_.getCount();
        }).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack pStack)
    {
        CompoundTag compoundtag = pStack.getOrCreateTag();
        if (!compoundtag.contains("Items")) {
            return Optional.empty();
        } else {
            ListTag listtag = compoundtag.getList("Items", 10);
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

    private static boolean dropContents(ItemStack pStack, Player pPlayer)
    {
        CompoundTag compoundtag = pStack.getOrCreateTag();
        if (!compoundtag.contains("Items")) {
            return false;
        } else {
            if (pPlayer instanceof ServerPlayer) {
                ListTag listtag = compoundtag.getList("Items", 10);

                for (int i = 0; i < listtag.size(); ++i) {
                    CompoundTag compoundtag1 = listtag.getCompound(i);
                    ItemStack itemstack = ItemStack.of(compoundtag1);
                    pPlayer.drop(itemstack, true);
                }
            }

            pStack.removeTagKey("Items");
            return true;
        }
    }

    private static Stream<ItemStack> getContents(ItemStack pStack)
    {
        CompoundTag compoundtag = pStack.getTag();
        if (compoundtag == null) {
            return Stream.empty();
        } else {
            ListTag listtag = compoundtag.getList("Items", 10);
            return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }
    // 存储相关基本逻辑 end
*/
/*
    @Override
    public boolean overrideStackedOnOther(ItemStack pStack, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer)
    {
        if (pStack.getCount() != 1 || pAction != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack itemstack = pSlot.getItem();
            if (itemstack.isEmpty()) {
                this.playRemoveOneSound(pPlayer);
                removeOne(pStack).ifPresent((p_150740_) -> {
                    add(pStack, pSlot.safeInsert(p_150740_));
                });
            } else if (itemstack.getItem().canFitInsideContainerItems()) {
                int i = (64 - getContentWeight(pStack)) / getWeight(itemstack);
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
                removeOne(pStack).ifPresent((p_186347_) -> {
                    this.playRemoveOneSound(pPlayer);
                    pAccess.set(p_186347_);
                });
            } else {
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

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, InteractionHand pUsedHand)
    {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (dropContents(itemstack, pPlayer)) {
            this.playDropContentsSound(pPlayer);
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    public boolean isBarVisible(@NotNull ItemStack pStack)
    {
        return getContentWeight(pStack) > 0;
    }

    public int getBarWidth(@NotNull ItemStack pStack)
    {
        return Math.min(1 + 12 * getContentWeight(pStack) / 64, 13);
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
    }*/

    @Inject(method = "appendHoverText", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/InstrumentItem;getInstrument(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;"))
    public void appendHoverTextInject(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced, CallbackInfo info)
    {
        pTooltipComponents.add(Component.translatable("aaaaaaaaaaaaaaaa"));
        System.out.println("-----------------appendHoverTextInject--------------------------------------------");
//        pTooltipComponents.add(Component.translatable("item.minecraft.bundle.fullness", getContentWeight(pStack), 64).withStyle(ChatFormatting.GRAY));
    }
/*

    public void onDestroyed(@NotNull ItemEntity pItemEntity)
    {
        ItemUtils.onContainerDestroyed(pItemEntity, getContents(pItemEntity.getItem()));
    }

    private void playRemoveOneSound(Entity pEntity)
    {
        pEntity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity pEntity)
    {
        pEntity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity pEntity)
    {
        pEntity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }
*/

}
