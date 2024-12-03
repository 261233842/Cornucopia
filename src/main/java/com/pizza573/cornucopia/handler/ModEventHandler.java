package com.pizza573.cornucopia.handler;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.init.ModDataComponents;
import com.pizza573.cornucopia.item.components.CornucopiaContents;
import com.pizza573.cornucopia.network.ServerBoundDropCornucopiaContentsPacket;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Cornucopia.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler
{
    // 注册 payload handler
    @SubscribeEvent
    public static void registerPayloadHandler(final RegisterPayloadHandlersEvent event)
    {
        // 设置版本
        PayloadRegistrar registrar = event.registrar("1");
        // 默认在main thread进行处理；也可以设置为network thread
        // registrar = registrar.executesOn(HandlerThread.NETWORK);
        // 注册发送到服务器的packet
        registrar.playToServer(
                // 类型 类似于id
                ServerBoundDropCornucopiaContentsPacket.TYPE,
                ServerBoundDropCornucopiaContentsPacket.STREAM_CODEC,
                // 处理packet
                ServerBoundDropCornucopiaContentsPacket::handle
        );
    }

    // 给原版物品添加 DataComponent
    @SubscribeEvent
    public static void modifyComponents(ModifyDefaultComponentsEvent event)
    {
        event.modify(Items.GOAT_HORN, builder -> builder.set(ModDataComponents.CORNUCOPIA_CONTENTS.get(), CornucopiaContents.EMPTY));
    }
}
