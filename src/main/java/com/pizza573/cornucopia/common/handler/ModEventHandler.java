package com.pizza573.cornucopia.common.handler;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.common.registry.ModDataComponents;
import com.pizza573.cornucopia.common.item.components.CornucopiaContents;
import com.pizza573.cornucopia.common.network.ServerBoundDropCornucopiaContentsPacket;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Cornucopia.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler
{
    @SubscribeEvent // 注册 payload handler
    public static void registerPayloadHandler(final RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar("1"); // 设置版本

        // 默认在main thread进行处理，也可以设置为network thread
        // registrar = registrar.executesOn(HandlerThread.NETWORK);

        registrar.playToServer( // 注册发送到服务器的packet
                ServerBoundDropCornucopiaContentsPacket.TYPE, // 类型 类似于id
                ServerBoundDropCornucopiaContentsPacket.STREAM_CODEC,
                ServerBoundDropCornucopiaContentsPacket::handle // 处理packet
        );
    }


    @SubscribeEvent // 给原版物品添加 DataComponent
    public static void modifyComponents(ModifyDefaultComponentsEvent event)
    {
        event.modify(Items.GOAT_HORN, builder -> builder.set(ModDataComponents.CORNUCOPIA_CONTENTS.get(), CornucopiaContents.EMPTY));
    }
}
