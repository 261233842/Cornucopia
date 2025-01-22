package com.pizza573.cornucopia.util;

import com.pizza573.cornucopia.init.ModDataComponents;
import com.pizza573.cornucopia.item.components.CornucopiaContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class FoodSelectHelper
{
    public static FoodProperties getFoodPropertiesOfSuitableFood(Player player, ItemStack cornucopia)
    {
        int suitableFoodIndex = getSuitableFoodIndex(player, cornucopia);
        ItemStack suitableFood = getSingleFood(cornucopia, suitableFoodIndex);
        return suitableFood.getFoodProperties(player);
    }

    public static void removeSingleFood(ItemStack cornucopia, int index)
    {
        CornucopiaContents cornucopiaContents = cornucopia.get(ModDataComponents.CORNUCOPIA_CONTENTS);
        if (cornucopiaContents != null) {
            CornucopiaContents.Mutable cornucopiaContents$mutable = new CornucopiaContents.Mutable(cornucopiaContents);
            cornucopiaContents$mutable.removeSingle(index);
            cornucopia.set(ModDataComponents.CORNUCOPIA_CONTENTS, cornucopiaContents$mutable.toImmutable());
        }
    }

    public static ItemStack getSingleFood(ItemStack cornucopia, int index)
    {
        CornucopiaContents cornucopiaContents = cornucopia.get(ModDataComponents.CORNUCOPIA_CONTENTS);
        if (cornucopiaContents != null) {
            CornucopiaContents.Mutable cornucopiaContents$mutable = new CornucopiaContents.Mutable(cornucopiaContents);
            ItemStack food = cornucopiaContents$mutable.getOne(index);
            cornucopia.set(ModDataComponents.CORNUCOPIA_CONTENTS, cornucopiaContents$mutable.toImmutable());
            return food;
        }
        return ItemStack.EMPTY;
    }

    public static int getSuitableFoodIndex(Player player, ItemStack cornucopia)
    {
        CornucopiaContents contents = cornucopia.getOrDefault(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
        List<ItemStack> items = (List<ItemStack>) contents.items();

        // 处理边界条件
        if (items.isEmpty()) {
            return 0; // 或者抛出异常
        }

        // 长度为1
        if (items.size() == 1) {
            return 0;
        }

        int index = 0;
        int score = 20;
        int nutrition = 0;

        int foodLevel = player.getFoodData().getFoodLevel();
        float health = player.getHealth();

        for (int i = 0; i < items.size(); i++) {
            ItemStack itemstack = items.get(i);
            FoodProperties foodProperties = itemstack.getItem().getFoodProperties(itemstack, player);

            if (foodProperties == null) {
                continue; // 跳过无效的物品
            }

            if (foodLevel == 20 && foodProperties.canAlwaysEat()) {
                return i;
            } else if (foodLevel < 20) {// 饥饿值非满
                // 不是（附魔）金苹果，根据饥饿值选择最合适的食物，最后返回
                if (!isGoldenApple(itemstack)) {
                    int newNutrition = foodProperties.nutrition();
                    int newScore = Math.abs(20 - (foodLevel + newNutrition));
                    // todo 目前不考虑饱和度
                    if (newScore < score || (newScore == score && newNutrition > nutrition)) {
                        index = i;
                        score = newScore;
                    }
                }
                //根据生命值，选择合适的食物
                if (health > 6 && health <= 10f && itemstack.getItem() == Items.GOLDEN_CARROT)
                    return i;
                else if (health <= 6f && isGoldenApple(itemstack)) // 低生命值，吃（附魔）金苹果
                    return i;
            }
        }

        return index;
    }

    public static boolean isGoldenApple(ItemStack itemstack)
    {
        return itemstack.getItem() == Items.GOLDEN_APPLE || itemstack.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
    }
}
