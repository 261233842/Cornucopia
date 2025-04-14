package com.pizza573.cornucopia.data;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.data.advancement.ModAdvancementProvider;
import com.pizza573.cornucopia.data.loot.ModLootTableProvider;
import com.pizza573.cornucopia.data.tag.ModBlockTagsProvider;
import com.pizza573.cornucopia.data.tag.ModItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Cornucopia.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModDataGenerator
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // other providers here
        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ModItemTagsProvider(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new ModAdvancementProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModLootTableProvider(output, Set.of(), List.of(), lookupProvider));
        generator.addProvider(event.includeServer(), new ModDatapackBuiltinEntriesProvider(output, lookupProvider));
    }
}
