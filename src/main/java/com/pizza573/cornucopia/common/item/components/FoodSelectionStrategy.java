package com.pizza573.cornucopia.common.item.components;

import com.pizza573.cornucopia.Cornucopia;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public enum FoodSelectionStrategy
{
    CAN_ALWAYS_EAT {
        @Override
        public int selectFoodIndex(Player player, List<ItemStack> items)
        {
            for (int i = 0; i < items.size(); i++) {
                ItemStack foodItemStack = items.get(i);
                FoodProperties foodProperties = foodItemStack.getFoodProperties(player);

                if (foodProperties != null && foodProperties.canAlwaysEat()) {
                    return i;
                }
            }

            return DEFAULT_INDEX;
        }
    },
    MAX_NUTRITION {
        @Override
        public int selectFoodIndex(Player player, List<ItemStack> items)
        {
            int foodLevel = player.getFoodData().getFoodLevel();
            int pre_score = 20;
            int pre_nutrition = 0;
            int index = DEFAULT_INDEX;

            for (int i = 0; i < items.size(); i++) {
                ItemStack foodItemStack = items.get(i);
                FoodProperties foodProperties = foodItemStack.getFoodProperties(player);

                if (foodProperties != null && !isGoldenApple(foodItemStack)) {
                    int aft_nutrition = foodProperties.nutrition();
                    int aft_score = Math.abs(20 - (foodLevel + aft_nutrition));

                    if (aft_score < pre_score || (aft_score == pre_score && aft_nutrition > pre_nutrition)) {
                        index = i;
                        pre_score = aft_score;
                    }
                }
            }

            return index;
        }

        private boolean isGoldenApple(ItemStack itemStack)
        {
            return itemStack.getItem() == Items.ENCHANTED_GOLDEN_APPLE || itemStack.getItem() == Items.GOLDEN_APPLE;
        }
    },
    GOLDEN_APPLE {
        @Override
        public int selectFoodIndex(Player player, List<ItemStack> items)
        {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getItem() == Items.GOLDEN_APPLE) {
                    Cornucopia.LOGGER.info("golden apple index:{}", i);
                    return i;
                }
            }
            return DEFAULT_INDEX; // 如果没有普通金苹果，返回 -1
        }
    },
    ENCHANTED_APPLE {
        @Override
        public int selectFoodIndex(Player player, List<ItemStack> items)
        {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                    return i;
                }
            }
            return DEFAULT_INDEX;
        }
    };


    final int DEFAULT_INDEX = 0;

    public abstract int selectFoodIndex(Player player, List<ItemStack> items);
}
