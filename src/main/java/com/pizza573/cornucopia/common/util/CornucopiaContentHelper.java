package com.pizza573.cornucopia.common.util;

import com.pizza573.cornucopia.Config;
import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.common.registry.ModDataComponents;
import com.pizza573.cornucopia.common.item.components.CornucopiaContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

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
        Map<Integer, FoodSelectionStrategy> strategies;

        // 处理特殊条件
        if (items.isEmpty()) return 0;
        if (items.size() == 1) return 0;

        float health = player.getHealth();
        float thresholdValue = Config.COMMON.lifeThresholdValue.get();
        int index = -1;

        // 满饥饿值:附魔金>金>canAlwaysEat
        if (!player.getFoodData().needsFood()) {
            Cornucopia.LOGGER.info("Full hunger value strategy.");
            strategies = Map.of(
                    0, FoodSelectionStrategy.ENCHANTED_APPLE,
                    1, FoodSelectionStrategy.GOLDEN_APPLE,
                    2, FoodSelectionStrategy.CAN_ALWAYS_EAT
            );

            for (int i = 0; i < 3; i++) {
                if (index == -1) {
                    Cornucopia.LOGGER.info("strategy:{}", strategies.get(i));
                    index = strategies.get(i).selectFoodIndex(player, items);
                    Cornucopia.LOGGER.info("selectFoodIndex:{}", index);
                } else {
                    break;
                }
            }
        }

        // 非满饥饿值
        if (player.getFoodData().needsFood()) {
            Cornucopia.LOGGER.info("Not Full hunger value strategy.");
            if (health <= thresholdValue) {// 低生命值:附魔金>金>MaxNutrition
                strategies = Map.of(
                        0, FoodSelectionStrategy.ENCHANTED_APPLE,
                        1, FoodSelectionStrategy.GOLDEN_APPLE,
                        2, FoodSelectionStrategy.MAX_NUTRITION
                );

                for (int i = 0; i < 3; i++) {
                    if (index == -1) {
                        index = strategies.get(i).selectFoodIndex(player, items);
                    } else {
                        break;
                    }
                }
            } else {// 高生命值:附魔金>金（size=2）or MaxNutrition最先
                if (items.size() == 2 && isGoldenApple(items.get(0)) && isGoldenApple(items.get(1))) {
                    if(items.getFirst().getItem()==Items.ENCHANTED_GOLDEN_APPLE){
                        index = 0;
                    }else{
                        index=1;
                    }
                } else {
                    index=FoodSelectionStrategy.MAX_NUTRITION.selectFoodIndex(player, items);
                }
            }
        }

        Cornucopia.LOGGER.info("SelectedFoodIndex:{}, SelectedFood:{}", index, items.get(index));
        return index;
    }

    public static boolean isGoldenApple(ItemStack itemstack)
    {
        return itemstack.getItem() == Items.GOLDEN_APPLE || itemstack.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
    }
}