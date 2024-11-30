package com.pizza573.cornucopia.handler;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.init.ModKeys;
import com.pizza573.cornucopia.item.CornucopiaItem;
import com.pizza573.cornucopia.network.ServerBoundDropCornucopiaContentsPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Cornucopia.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class NeoforgeClientEventHandler
{
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event)
    {
        while (ModKeys.DROP_CORNUCOPIA_CONTENTS.consumeClick()) {
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getMainHandItem().getItem() instanceof CornucopiaItem) {
                // 发包到服务器处理
                PacketDistributor.sendToServer(new ServerBoundDropCornucopiaContentsPacket("ServerBoundDropCornucopiaContentsPacket发包到服务器"));
            }
        }
    }
}