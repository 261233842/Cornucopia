package com.pizza573.cornucopia;

import com.pizza573.cornucopia.init.ModDataComponents;
import com.pizza573.cornucopia.init.ModCreativeTabs;
import com.pizza573.cornucopia.init.ModItems;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Cornucopia.MOD_ID)
public class Cornucopia
{
    public static final String MOD_ID = "cornucopia";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Cornucopia(IEventBus modEventBus, ModContainer modContainer)
    {
        ModItems.REGISTER.register(modEventBus);
        ModCreativeTabs.REGISTER.register(modEventBus);
        ModDataComponents.REGISTRAR.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON,Config.CONFIG_SPEC);
        // This will use NeoForge's ConfigurationScreen to display this mod's configs
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
