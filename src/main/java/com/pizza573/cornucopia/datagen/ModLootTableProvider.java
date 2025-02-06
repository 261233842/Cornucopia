package com.pizza573.cornucopia.datagen;

import com.pizza573.cornucopia.Cornucopia;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ModLootTableProvider extends LootTableProvider
{
    public ModLootTableProvider(PackOutput output, Set<ResourceKey<LootTable>> requiredTables, List<SubProviderEntry> subProviders, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(
                output
                , Set.of()
                , List.of(new SubProviderEntry(ModLootTableSubProvider::new, LootContextParamSets.EMPTY))
                , registries
        );
    }

    private static final class ModLootTableSubProvider implements LootTableSubProvider
    {
        public ModLootTableSubProvider(HolderLookup.Provider lookupProvider)
        {
            super();
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output)
        {
            // LootTable.lootTable() returns a loot table builder we can add loot tables to.
            output.accept(
                    ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Cornucopia.MOD_ID, "main/one_goat_horn")),
                    LootTable.lootTable()
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.GOAT_HORN)))
            );

            output.accept(
                    ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Cornucopia.MOD_ID, "main/three_golden_apple")),
                    LootTable.lootTable()
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3)))
                            .withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.GOLDEN_APPLE))));
        }
    }
}

