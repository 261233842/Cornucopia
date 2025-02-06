package com.pizza573.cornucopia.handler;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.init.ModItems;
import com.pizza573.cornucopia.util.FoodSelectHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Cornucopia.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class NeoforgeEventHandler
{
    static byte i = 0;
    static boolean appleSkinIsLoaded = ModList.get().isLoaded("appleskin");

    @SubscribeEvent
    public static void updateFoodProperties(PlayerTickEvent.Post event)
    {
        i %= 20;
        if (appleSkinIsLoaded && i++ == 0 && event.getEntity() instanceof ServerPlayer player) {
            ItemStack cornucopia = player.getMainHandItem();

            if (cornucopia.getItem() == ModItems.CORNUCOPIA.get()) {
                int suitableFoodIndex = FoodSelectHelper.getSuitableFoodIndex(player, cornucopia);
                ItemStack suitableFood = FoodSelectHelper.getSingleFood(cornucopia, suitableFoodIndex);
//                System.out.println("合适的食物suitableFood: "+suitableFood);
                FoodProperties foodProperties = suitableFood.getFoodProperties(player);
//                if(foodProperties!=null) System.out.println("nutrition: "+foodProperties.nutrition());
                cornucopia.set(DataComponents.FOOD, foodProperties);
            }
        }
    }
}
