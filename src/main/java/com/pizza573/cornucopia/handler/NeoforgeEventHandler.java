package com.pizza573.cornucopia.handler;

import com.pizza573.cornucopia.Config;
import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Cornucopia.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class NeoforgeEventHandler
{
    // 第一次进世界，给予CornucopiaItem
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getEntity() instanceof ServerPlayer player){
            if(Config.COMMON.loginWithCornucopia.get() && !player.getPersistentData().contains("HasJoinedBefore")){
                player.getPersistentData().putBoolean("HasJoinedBefore",true);
                player.getInventory().add(new ItemStack(ModItems.CORNUCOPIA.get()));
            }
        }
    }
}
