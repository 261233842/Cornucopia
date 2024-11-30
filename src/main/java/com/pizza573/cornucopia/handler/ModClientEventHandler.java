package com.pizza573.cornucopia.handler;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.client.screens.tooltip.ClientCornucopiaTooltip;
import com.pizza573.cornucopia.init.ModItems;
import com.pizza573.cornucopia.init.ModKeys;
import com.pizza573.cornucopia.client.screens.tooltip.CornucopiaTooltip;
import com.pizza573.cornucopia.item.CornucopiaItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

//添加了@EventBusSubscriber注释就无需在主类注册
@EventBusSubscriber(modid = Cornucopia.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModClientEventHandler
{
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event)
    {
        event.register(ModKeys.DROP_CORNUCOPIA_CONTENTS);
    }

    @SubscribeEvent
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event)
    {
        // 注册提示框
        event.register(CornucopiaTooltip.class, ClientCornucopiaTooltip::new);
    }

    @SubscribeEvent
    public static void propertyOverride(FMLClientSetupEvent event)
    {
        // 添加自定义物品渲染
        ItemProperties.register(
                ModItems.CORNUCOPIA.get(),
                ResourceLocation.fromNamespaceAndPath(Cornucopia.MOD_ID, "weight"),
                (stack, level, entity, seed) -> CornucopiaItem.getWeightDisplay(stack)
        );
    }
}
