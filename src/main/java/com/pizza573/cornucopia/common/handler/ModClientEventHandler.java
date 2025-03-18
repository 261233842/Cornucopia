package com.pizza573.cornucopia.common.handler;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.client.screens.tooltip.ClientCornucopiaTooltip;
import com.pizza573.cornucopia.common.registry.ModItems;
import com.pizza573.cornucopia.common.registry.ModKeys;
import com.pizza573.cornucopia.client.screens.tooltip.CornucopiaTooltip;
import com.pizza573.cornucopia.common.item.CornucopiaItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

// 添加了 @EventBusSubscriber 注释就无需在主类注册
@EventBusSubscriber(modid = Cornucopia.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModClientEventHandler
{
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event)
    {
        event.register(ModKeys.DROP_CORNUCOPIA_CONTENTS);
    }

    @SubscribeEvent // 注册提示框
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event)
    {
        event.register(CornucopiaTooltip.class, ClientCornucopiaTooltip::new);
    }

    @SubscribeEvent // 添加自定义物品渲染，通过 weight 切换丰饶角贴图
    public static void propertyOverride(FMLClientSetupEvent event)
    {
        ItemProperties.register(
                ModItems.CORNUCOPIA.get(),
                ResourceLocation.fromNamespaceAndPath(Cornucopia.MOD_ID, "weight"),
                (stack, level, entity, seed) -> CornucopiaItem.getWeightDisplay(stack)
        );
    }
}
