package com.pizza573.cornucopia.common.util;

import com.pizza573.cornucopia.Config;
import com.pizza573.cornucopia.common.registry.ModDataComponents;
import com.pizza573.cornucopia.common.item.components.CornucopiaContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CornucopiaContentHelper
{
    public static void removeOneItem(ItemStack cornucopia, int index)
    {
        CornucopiaContents cornucopiaContents = cornucopia.get(ModDataComponents.CORNUCOPIA_CONTENTS);
        if (cornucopiaContents != null) {
            CornucopiaContents.Mutable cornucopiaContents$mutable = new CornucopiaContents.Mutable(cornucopiaContents);
            cornucopiaContents$mutable.removeOneItem(index);
            cornucopia.set(ModDataComponents.CORNUCOPIA_CONTENTS, cornucopiaContents$mutable.toImmutable());
        }
    }

    public static ItemStack getItemStackCopy(ItemStack cornucopia, int index)
    {
        CornucopiaContents cornucopiaContents = cornucopia.get(ModDataComponents.CORNUCOPIA_CONTENTS);
        if (cornucopiaContents != null) {
            CornucopiaContents.Mutable cornucopiaContents$mutable = new CornucopiaContents.Mutable(cornucopiaContents);
            ItemStack food = cornucopiaContents$mutable.getItemStackCopy(index);
            cornucopia.set(ModDataComponents.CORNUCOPIA_CONTENTS, cornucopiaContents$mutable.toImmutable());
            return food;
        }
        return ItemStack.EMPTY;
    }

    // 优先级 附魔金>普通金苹果>canAlwaysEat>吃饱
    public static int getSuitableFoodIndex(Player player, ItemStack cornucopia)
    {
        CornucopiaContents contents = cornucopia.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
        List<ItemStack> items = (List<ItemStack>) contents.items();

        // 处理特殊条件
        if (items.isEmpty()) return 0;
        if (items.size() == 1) return 0;

        int foodLevel = player.getFoodData().getFoodLevel();// 饥饿值
        float health = player.getHealth();
        float thresholdValue = Config.COMMON.lifeThresholdValue.get();
        int index = 0;
        // 初始化（附魔）金苹果、canAlwaysEat 和 canAlwaysEat 索引
        int enchantedGoldenApple_i = -1;
        int goldenApple_i = -1;
        int canAlwaysEat_i = -1;

        int pre_nutrition = 0;
        int pre_score = 20;

        for (int i = 0; i < items.size(); i++) {
            ItemStack foodItemStack = items.get(i);
            Item item = items.get(i).getItem();
            FoodProperties foodProperties = foodItemStack.getFoodProperties(player);

            if (foodProperties == null) continue;

            // switch case 不支持布尔值判断
            if (item == Items.ENCHANTED_GOLDEN_APPLE) enchantedGoldenApple_i = i;
            if (item == Items.GOLDEN_APPLE) goldenApple_i = i;
            if (foodProperties.canAlwaysEat()) canAlwaysEat_i = i;

            // 饥饿值非满，不能吃（附魔）金苹果
            if (player.getFoodData().needsFood() && !isGoldenApple(foodItemStack)) {
                // 更新尽量吃饱的食物的索引
                int aft_nutrition = foodProperties.nutrition();
                int aft_score = Math.abs(20 - (foodLevel + aft_nutrition));
                if (aft_score < pre_score || (aft_score == pre_score && aft_nutrition > pre_nutrition)) {
                    index = i;
                    pre_score = aft_score;
                }
            }
        }

        // 满饥饿值
        if (!player.getFoodData().needsFood())
            // 附魔金>普金>canAlwaysEat
            if (enchantedGoldenApple_i != -1) index = enchantedGoldenApple_i;
            else if (goldenApple_i != -1) index = goldenApple_i;
            else if (canAlwaysEat_i != -1) index = canAlwaysEat_i;

        // 非满饥饿值 低生命值
        if (player.getFoodData().needsFood()) {
            if (health <= thresholdValue) {// 低生命值，附魔金>普金>尽可能吃到满饥饿值
                if (enchantedGoldenApple_i != -1) index = enchantedGoldenApple_i;
                else if (goldenApple_i != -1) index = goldenApple_i;
            } else {// 高生命值
                if (items.size() == 2 && isGoldenApple(items.get(0)) && isGoldenApple(items.get(1))) {
                    if (enchantedGoldenApple_i != -1) index = enchantedGoldenApple_i;
                    else if (goldenApple_i != -1) index = goldenApple_i;
                }
            }
        }

        return index;
    }

    public static boolean isGoldenApple(ItemStack itemstack)
    {
        return itemstack.getItem() == Items.GOLDEN_APPLE || itemstack.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
    }
}