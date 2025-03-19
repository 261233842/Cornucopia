package com.pizza573.cornucopia.common.registry;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.common.item.components.CornucopiaContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

//  hold the information that you need to implement a given effect component
public class ModDataComponents
{
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Cornucopia.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> ENCHANTMENT_EFFECT_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Cornucopia.MOD_ID);

    // data component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CornucopiaContents>> CORNUCOPIA_CONTENTS = REGISTER.registerComponentType(
            "cornucopia_contents",
            builder -> builder
                    .persistent(CornucopiaContents.CODEC) // The codec to read/write the data to disk
                    .networkSynchronized(CornucopiaContents.STREAM_CODEC) // The codec to read/write the data across the network
                    .cacheEncoding()
    );

    // enchantment effect component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> NUTRITION = ENCHANTMENT_EFFECT_COMPONENT_TYPES.register(
            "nutrition",
            () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                    .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()) // todo 第二个 ENCHANTED_DAMAGE
                    .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> COPIOUS = ENCHANTMENT_EFFECT_COMPONENT_TYPES.register(
            "copious",
            () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                    .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()) // todo 第二个 ENCHANTED_DAMAGE
                    .build()
    );
}
