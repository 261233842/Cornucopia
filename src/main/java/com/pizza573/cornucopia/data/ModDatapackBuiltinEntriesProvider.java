package com.pizza573.cornucopia.data;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.data.enchantment.ModEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

// 用于提供自定义的数据包内置条目
public class ModDatapackBuiltinEntriesProvider extends DatapackBuiltinEntriesProvider
{
    // 于注册附魔、维度相关的条目
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.ENCHANTMENT, ModEnchantments::bootstrap);

    public ModDatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries, BUILDER, Set.of(Cornucopia.MOD_ID));
    }
}
