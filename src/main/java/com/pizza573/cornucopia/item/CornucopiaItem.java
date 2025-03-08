package com.pizza573.cornucopia.item;

import com.pizza573.cornucopia.client.screens.tooltip.CornucopiaTooltip;
import com.pizza573.cornucopia.item.components.CornucopiaContents;
import com.pizza573.cornucopia.init.ModDataComponents;
import com.pizza573.cornucopia.util.FoodSelectHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CornucopiaItem extends Item
{
    // static 变量，所有对象共享同一个静态字段值，示例：计数器、配置常量等。
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
    private static final int ITEM_NAME_COLOR= Mth.color(0.133f, 0.545f, 0.133f);// 暗绿色
    private static final int REDUCE_TIME = 6; // 0.3s * 20tick/s = 6tick
    private int suitableFoodIndex;
    private ItemStack suitableFood = ItemStack.EMPTY;

    public CornucopiaItem(Properties properties)
    {
        super(properties);
    }


    @Override
    public @NotNull Component getName(@NotNull ItemStack stack)
    {
        return Component.translatable(super.getName(stack).getString()).withColor(ITEM_NAME_COLOR);
    }

    // 供物品属性weight使用，类似boson的magicIngot，“使物品能够动态的切换贴图”
    public static float getWeightDisplay(ItemStack stack)
    {
        CornucopiaContents cornucopiaContents = stack.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
        // todo 兼容容量附魔
        return cornucopiaContents.weight().floatValue() / 2f/*除以附魔等级*/;
    }

    // slotClicked
    @Override
    public boolean overrideStackedOnOther(ItemStack stack, @NotNull Slot slot, @NotNull ClickAction action, @NotNull Player player)
    {
//        System.out.println("overrideStackOnOther start");
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
//                    System.out.println("remove one type food: " + other.getItem());
                    this.playRemoveOneSound(player);
                    // 移除单个种类的物品
                    ItemStack itemstack1 = cornucopiaContents$mutable.removeOne();
                    if (itemstack1 != null) {
                        // 存入物品到slot，return未存入的
                        ItemStack itemstack2 = slot.safeInsert(itemstack1);
                        cornucopiaContents$mutable.tryInsert(itemstack2);
                    }
                } else if (other.getItem().canFitInsideContainerItems() && other.getFoodProperties(player) != null) {
//                    System.out.println("insert one type food: " + other.getItem());
                    int i = cornucopiaContents$mutable.tryTransfer(slot, player);
                    if (i > 0) this.playInsertSound(player);
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

    // 右键使用 Cornucopia
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand)
    {
        ItemStack cornucopia = player.getItemInHand(usedHand);
        CornucopiaContents contents = cornucopia.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
        // 判空
        if (contents.isEmpty()) return InteractionResultHolder.pass(cornucopia);

        // 获取选择好的食物的 FoodProperties
        suitableFoodIndex = FoodSelectHelper.getSuitableFoodIndex(player, cornucopia);
        suitableFood = FoodSelectHelper.getSingleFood(cornucopia, suitableFoodIndex);
        FoodProperties foodProperties = suitableFood.getFoodProperties(player);

        // 吃食物
        if (player.canEat(foodProperties != null && foodProperties.canAlwaysEat())) {
            // 告诉系统开始使用物品
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(cornucopia);
        } else {
            return InteractionResultHolder.fail(cornucopia);
        }
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity)
    {
        suitableFood.getItem().finishUsingItem(suitableFood, level, livingEntity);
        FoodSelectHelper.removeSingleFood(stack, suitableFoodIndex);
        // 返回最初的stack，即CornucopiaItem
        return stack;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack)
    {
        return suitableFood.getItem().getUseAnimation(suitableFood);
    }

    // 获取使用时间
    @Override
    public int getUseDuration(@NotNull ItemStack cornucopia, @NotNull LivingEntity entity)
    {
        FoodProperties foodProperties = suitableFood.getFoodProperties(entity);
        // 一般食物的使用时间为1.6s，海带0.8s
        return foodProperties != null ? foodProperties.eatDurationTicks() - REDUCE_TIME : 0;
    }

    // 是否显示bar条（耐久度bar、收纳袋容量bar）
    @Override
    public boolean isBarVisible(ItemStack stack)
    {
        CornucopiaContents cornucopiaContents = stack.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
        return cornucopiaContents.weight().compareTo(Fraction.ZERO) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        CornucopiaContents cornucopiaContents = stack.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
        // 重量*12+1并向下取整，再取其和13的最小值 todo 兼容容量附魔
        return Math.min(1 + Mth.mulAndTruncate(cornucopiaContents.weight(), 12 / 2/*除以容量附魔等级*/), 13);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack)
    {
        return BAR_COLOR;
    }

    // 容量ui
    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        // Optional.ofNullable(...) 返回Optional对象，如果参数为null，则返回Optional.empty()，否则返回Optional.of(...)
        // if (没有两个要隐藏的数据组件)->返回BundleTooltip对象
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(stack.get(ModDataComponents.CORNUCOPIA_CONTENTS)).map(CornucopiaTooltip::new) : Optional.empty();
    }

    // 添加文本
    @Override
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext
            context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag)
    {
        CornucopiaContents cornucopiaContents = stack.get(ModDataComponents.CORNUCOPIA_CONTENTS);
        if (cornucopiaContents != null) {
            // weight()的分子*64/weight()的分母 -> 向下取整
            int foodValues = Mth.mulAndTruncate(cornucopiaContents.weight(), 64);
            // "容量权重"前端渲染修改
            tooltipComponents.add(Component.translatable("item.minecraft.cornucopia.fullness", foodValues, 64 * 2/*乘以容量等级*/).withStyle(ChatFormatting.GRAY));
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable("item.minecraft.cornucopia.description").withColor(Mth.color(0.133f, 0.545f, 0.133f)));
            }
        }
    }

    // 不能被放入背包之类的容器物品，不影响部分容器实体，如：chest
    public boolean canFitInsideContainerItems()
    {
        return false;
    }

    private void playRemoveOneSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public @NotNull SoundEvent getDrinkingSound()
    {
        return suitableFood.getItem().getDrinkingSound();
    }

    @Override
    public @NotNull SoundEvent getEatingSound()
    {
        return suitableFood.getItem().getEatingSound();
    }
}
