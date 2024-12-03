package com.pizza573.cornucopia.mixin;

import com.pizza573.cornucopia.client.screens.tooltip.CornucopiaTooltip;
import com.pizza573.cornucopia.init.ModDataComponents;
import com.pizza573.cornucopia.init.ModItems;
import com.pizza573.cornucopia.item.components.CornucopiaContents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(InstrumentItem.class)
public class InstrumentItemMixin extends Item
{
    @Unique
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);


    public InstrumentItemMixin(Properties properties)
    {
        super(properties);
    }

    @Inject(method = "appendHoverText",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/InstrumentItem;getInstrument(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;"))
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag, CallbackInfo info){
        CornucopiaContents cornucopiaContents = stack.get(ModDataComponents.CORNUCOPIA_CONTENTS);
        if (cornucopiaContents != null) {
            int i = Mth.mulAndTruncate(cornucopiaContents.weight(), 64);
            tooltipComponents.add(Component.translatable("item.minecraft.cornucopia.fullness", i, 64).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player)
    {
        if (stack.getCount() != 1 || action != ClickAction.SECONDARY) {
            return false;
        } else {
            CornucopiaContents cornucopiaContents = stack.get(ModDataComponents.CORNUCOPIA_CONTENTS);
            if (cornucopiaContents == null) {
                return false;
            } else {
                ItemStack other = slot.getItem();
                CornucopiaContents.Mutable cornucopiaContents$mutable = new CornucopiaContents.Mutable(cornucopiaContents);
                if (other.isEmpty()) {
                    this.playRemoveOneSound(player);
                    // 移除单个种类的物品
                    ItemStack itemstack1 = cornucopiaContents$mutable.removeOne();
                    if (itemstack1 != null) {
                        // 存入物品到slot，return未存入的
                        ItemStack itemstack2 = slot.safeInsert(itemstack1);
                        cornucopiaContents$mutable.tryInsert(itemstack2);
                    }
                    // 只能存入food（参考 Item 的 finishUsingItem(...)，最初调用的比较底层）
                } else if (other.getItem().canFitInsideContainerItems() && other.getFoodProperties(player) != null) {
                    int i = cornucopiaContents$mutable.tryTransfer(slot, player);
                    if (i > 0) {
                        this.playInsertSound(player);
                    }
                }

                stack.set(ModDataComponents.CORNUCOPIA_CONTENTS, cornucopiaContents$mutable.toImmutable());
                return true;
            }
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, @NotNull ItemStack other, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player, @NotNull SlotAccess access)
    {
        if (stack.getCount() != 1) return false;
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            CornucopiaContents cornucopiaContents = stack.get(ModDataComponents.CORNUCOPIA_CONTENTS);
            if (cornucopiaContents == null) {
                return false;
            } else {
                CornucopiaContents.Mutable cornucopiaContents$mutable = new CornucopiaContents.Mutable(cornucopiaContents);
                if (other.isEmpty()) {
                    ItemStack itemstack = cornucopiaContents$mutable.removeOne();
                    if (itemstack != null) {
                        this.playRemoveOneSound(player);
                        access.set(itemstack);
                    }
                    // 只能存入food（参考 Item 的 finishUsingItem(...)，最初调用的比较底层）
                } else if (other.getFoodProperties(player) != null) {
                    int i = cornucopiaContents$mutable.tryInsert(other);
                    if (i > 0) {
                        this.playInsertSound(player);
                    }
                }

                stack.set(ModDataComponents.CORNUCOPIA_CONTENTS, cornucopiaContents$mutable.toImmutable());
                return true;
            }
        } else {
            return false;
        }
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity)
    {
        CornucopiaContents cornucopiaContents=stack.get(ModDataComponents.CORNUCOPIA_CONTENTS);
        if (cornucopiaContents != null && cornucopiaContents.weight().compareTo(Fraction.ONE) == 0) {
            ItemStack cornucopiaItemStack = new ItemStack(ModItems.CORNUCOPIA.get());
            cornucopiaItemStack.set(ModDataComponents.CORNUCOPIA_CONTENTS, cornucopiaContents);
            return cornucopiaItemStack;
        }
        // 返回最初的stack，即CornucopiaItem
        return stack;
    }

    public boolean isBarVisible(ItemStack stack)
    {
        CornucopiaContents cornucopiaContents = stack.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
        return cornucopiaContents.weight().compareTo(Fraction.ZERO) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        CornucopiaContents cornucopiaContents = stack.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
        return Math.min(1 + Mth.mulAndTruncate(cornucopiaContents.weight(), 12), 13);
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        return BAR_COLOR;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(stack.get(ModDataComponents.CORNUCOPIA_CONTENTS)).map(CornucopiaTooltip::new) : Optional.empty();
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity)
    {
        CornucopiaContents cornucopiaContents = itemEntity.getItem().get(ModDataComponents.CORNUCOPIA_CONTENTS);
        if (cornucopiaContents != null) {
            itemEntity.getItem().set(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
            ItemUtils.onContainerDestroyed(itemEntity, cornucopiaContents.itemsCopy());
        }
    }

    public boolean canFitInsideContainerItems()
    {
        return false;
    }

    @Unique
    private void playRemoveOneSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    @Unique
    private void playInsertSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
