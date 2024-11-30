package com.pizza573.cornucopia;

import com.pizza573.cornucopia.init.ModDataComponents;
import com.pizza573.cornucopia.init.ModCreativeTabs;
import com.pizza573.cornucopia.init.ModItems;
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
    }
}
