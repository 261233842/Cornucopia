package com.pizza573.cornucopia.init;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.item.components.CornucopiaContents;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents
{
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Cornucopia.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CornucopiaContents>> CORNUCOPIA_CONTENTS = REGISTRAR.registerComponentType(
            "cornucopia_contents",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(CornucopiaContents.CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(CornucopiaContents.STREAM_CODEC)
                    .cacheEncoding()
    );
}
