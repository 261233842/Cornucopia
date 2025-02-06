package com.pizza573.cornucopia.init;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.enchantment.effect.Increment;
import com.pizza573.cornucopia.item.components.CornucopiaContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents
{
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Cornucopia.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> ENCHANTMENT_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, "examplemod");

    // data component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CornucopiaContents>> CORNUCOPIA_CONTENTS = REGISTER.registerComponentType(
            "cornucopia_contents",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(CornucopiaContents.CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(CornucopiaContents.STREAM_CODEC)
                    .cacheEncoding()
    );

    // enchantment effect component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Increment>> INCREMENT =
            ENCHANTMENT_COMPONENT_TYPES.register("increment",
                    () -> DataComponentType.<Increment>builder().persistent(Increment.CODEC).build()
            );

}
