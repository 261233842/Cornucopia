package com.pizza573.cornucopia;

import com.pizza573.cornucopia.init.ModCreativeTabs;
import com.pizza573.cornucopia.init.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Cornucopia.MOD_ID)
public class Cornucopia
{
    public static final String MOD_ID = "cornucopia";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Cornucopia()
    {
        // todo ???Register the commonSetup method for modloading???
        MinecraftForge.EVENT_BUS.register(this);
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.REGISTER.register(modEventBus);
        ModCreativeTabs.REGISTER.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC);
    }
}
