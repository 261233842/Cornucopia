package com.pizza573.cornucopia.util;

import com.pizza573.cornucopia.init.ModDataComponents;
import com.pizza573.cornucopia.item.components.CornucopiaContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Objects;

public class FoodSelectHelper
{
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
        if (items.size() == 1) return 0;

        // foodLevel 饥饿值
        int foodLevel = player.getFoodData().getFoodLevel();
        float health = player.getHealth();

        int index = 0;
        int pre_nutrition = Objects.requireNonNull(items.getFirst().getFoodProperties(player)).nutrition();
        int pre_score = Math.abs(20 - (foodLevel + pre_nutrition));

        for (int i = 1; i < items.size(); i++) {
            ItemStack foodItemStack = items.get(i);
            FoodProperties foodProperties = foodItemStack.getFoodProperties(player);

            // 跳过无效的物品
            if (foodProperties == null) continue;

            if (foodLevel == 20 && foodProperties.canAlwaysEat()) {
                return i;
            } else if (foodLevel < 20) {// 饥饿值非满
                // 不是（附魔）金苹果，根据饥饿值选择最合适的食物，最后返回
                if (!isGoldenApple(foodItemStack)) {
                    int aft_nutrition = foodProperties.nutrition();
                    int aft_score = Math.abs(20 - (foodLevel + aft_nutrition));
//                    System.out.println("pre_food"+items.get(i-1)+" aft_food"+foodItemStack);
//                    System.out.println("foodLevel:"+foodLevel);
//                    System.out.println("pre_nutrition:"+pre_nutrition+" aft_nutrition:"+aft_nutrition);
//                    System.out.println("pre_score:"+pre_score+" aft_score:"+aft_score);
                    if (aft_score < pre_score || (aft_score == pre_score && aft_nutrition > pre_nutrition)) {
                        index = i;
                        pre_score = aft_score;
                    }
//                    System.out.println("index:"+index);
                }

                //根据生命值，选择合适的食物，最后返回
                if (health > 6f && health <= 10f && foodItemStack.getItem() == Items.GOLDEN_CARROT)
                    return i;
                else if (health <= 6f && isGoldenApple(foodItemStack)){// todo 没有（附魔）金苹果时，有金胡萝卜时选择金胡萝卜
                    return i;
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
