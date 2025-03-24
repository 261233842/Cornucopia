package com.pizza573.cornucopia.common.item;

import com.pizza573.cornucopia.Config;
import com.pizza573.cornucopia.client.screens.tooltip.CornucopiaTooltip;
import com.pizza573.cornucopia.common.item.components.CornucopiaContents;
import com.pizza573.cornucopia.common.registry.ModDataComponents;
import com.pizza573.cornucopia.common.util.CornucopiaContentHelper;
import com.pizza573.cornucopia.data.enchantment.ModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CornucopiaItem extends Item
{
    // static 变量，所有对象共享同一个静态字段值，示例：计数器、配置常量等。
    private static final int COPIOUS_COLOR = Mth.color(0.133f, 0.545f, 0.133f);// 暗绿色
    private static final int REDUCE_TIME = 6; // 6tick = 0.3s * 20tick/s
    private static final int LEAST_TIME = 2;
    private static final int INIT_SIZE = 128;
    private int suitableFoodIndex;
    private ItemStack suitableFood = ItemStack.EMPTY;

    public CornucopiaItem(Properties properties)
    {
        super(properties);
    }

    public static int getMaxSize(ItemStack stack)
    {
        int level = 0;
        ItemEnchantments itemenchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY); // 获得物品的所有附魔

        for (Holder<Enchantment> holder : itemenchantments.keySet()) {
            ResourceKey<Enchantment> enchantmentKey = holder.getKey();
            if (enchantmentKey != null && enchantmentKey.isFor(ModEnchantments.CAPACITY.registryKey())) {
                level = itemenchantments.getLevel(holder);
            }
        }

        return level * 64 + INIT_SIZE;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack)
    {
        return Component.translatable(super.getName(stack).getString()).withColor(COPIOUS_COLOR);
    }

    // 供物品属性weight使用，类似boson的magicIngot，“使物品能够动态的切换贴图”
    public static float getWeightDisplay(ItemStack stack)
    {
        CornucopiaContents cornucopiaContents = stack.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
        int foodValues = Mth.mulAndTruncate(cornucopiaContents.weight(), 64);
        return (float) foodValues / getMaxSize(stack);
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
//                    System.out.println("remove one type food: " + other.getItem());
                    this.playRemoveOneSound(player);
                    // 移除单个种类的物品
                    ItemStack itemstack1 = cornucopiaContents$mutable.removeOneStack();
                    if (itemstack1 != null) {
                        ItemStack itemstack2 = slot.safeInsert(itemstack1); // 存入物品到slot，return未存入的
                        cornucopiaContents$mutable.tryInsert(itemstack2);
                    }
                } else if (other.getItem().canFitInsideContainerItems() && other.getFoodProperties(player) != null) {// 食物判断
                    if (cornucopiaContents$mutable.getMaxSize() != getMaxSize(stack)) {
                        cornucopiaContents$mutable.setMaxSize(getMaxSize(stack));// 刷新容量
                    }
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
                    ItemStack itemstack = cornucopiaContents$mutable.removeOneStack();
                    if (itemstack != null) {
                        this.playRemoveOneSound(player);
                        access.set(itemstack);
                    }
                } else if (other.getFoodProperties(player) != null) { // 食物判断
                    if (cornucopiaContents$mutable.getMaxSize() != getMaxSize(stack)) {
                        cornucopiaContents$mutable.setMaxSize(getMaxSize(stack));// 刷新容量
                    }
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

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand)
    {
        ItemStack cornucopia = player.getItemInHand(usedHand);
        CornucopiaContents contents = cornucopia.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);

        if (contents.isEmpty()) return InteractionResultHolder.pass(cornucopia); // 判空

        // 获取选择好的食物的 FoodProperties
        suitableFoodIndex = CornucopiaContentHelper.getSuitableFoodIndex(player, cornucopia);
        suitableFood = CornucopiaContentHelper.getItemStackCopy(cornucopia, suitableFoodIndex);
        FoodProperties foodProperties = suitableFood.getFoodProperties(player);

        // 吃食物
        if (player.canEat(foodProperties != null && foodProperties.canAlwaysEat())) {
            player.startUsingItem(usedHand); // 告诉系统开始使用物品
            return InteractionResultHolder.consume(cornucopia);
        } else {
            return InteractionResultHolder.fail(cornucopia);
        }
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity)
    {
        suitableFood.getItem().finishUsingItem(suitableFood, level, livingEntity);// 里头调用了livingEntity.eat(...)
        CornucopiaContentHelper.removeOneItem(stack, suitableFoodIndex);

        int random = ThreadLocalRandom.current().nextInt(100);
        if (livingEntity instanceof Player player) {
            if (random == 0) {
                ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.Y, level, livingEntity.blockPosition(), 1.2, ParticleTypes.DRAGON_BREATH, UniformInt.of(15, 20));
                player.drop(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), true);
            } else if (random <= 3) {
                ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.Y, level, livingEntity.blockPosition(), 1.2, ParticleTypes.HAPPY_VILLAGER, UniformInt.of(5, 10));
                player.drop(new ItemStack(Items.GOLDEN_APPLE), true);
            } else if (random <= 5) {
                ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.Y, level, livingEntity.blockPosition(), 1.2, ParticleTypes.HAPPY_VILLAGER, UniformInt.of(5, 10));
                player.drop(new ItemStack(Items.APPLE), true);
            }
        }

        return stack; // 返回最初的stack，即CornucopiaItem
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
        int useDuration = suitableFood.getUseDuration(entity); // 一般食物的使用时间为1.6s，海带0.8s
        return Math.max(useDuration - REDUCE_TIME, LEAST_TIME);
        //  return 6; // Test
    }

    // 杂项 Start
    @Override
    public boolean isEnchantable(@NotNull ItemStack stack)
    {
        return true;
    }

    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack)
    {
        return 30;
    }

    // 是否显示 bar 条（耐久度 bar 、收纳袋容量 bar）
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack)
    {
        if (!Config.COMMON.enableContentBar.get()) return false;
        else {
            CornucopiaContents cornucopiaContents = stack.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
            return cornucopiaContents.weight().compareTo(Fraction.ZERO) > 0;
        }

    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        CornucopiaContents cornucopiaContents = stack.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
        int foodValues = Mth.mulAndTruncate(cornucopiaContents.weight(), 64);
        Fraction proportion = Fraction.getFraction((double) foodValues / getMaxSize(stack));

        return Math.max(1, Mth.mulAndTruncate(proportion, 13));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack)
    {
        return COPIOUS_COLOR;
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
            tooltipComponents.add(Component.translatable("item.minecraft.cornucopia.fullness", foodValues, getMaxSize(stack)).withStyle(ChatFormatting.GRAY));
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable("item.minecraft.cornucopia.description").withColor(COPIOUS_COLOR));
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
    // 杂项 End
}
