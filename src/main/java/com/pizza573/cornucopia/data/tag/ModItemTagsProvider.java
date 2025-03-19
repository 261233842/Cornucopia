package com.pizza573.cornucopia.data.tag;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.common.registry.ModItems;
import com.pizza573.cornucopia.common.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider
{

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, blockTags, Cornucopia.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        tag(ModTags.ENCHANTABLE_CORNUCOPIA).addTag(ModTags.CORNUCOPIA);
        tag(ModTags.CORNUCOPIA).add(ModItems.CORNUCOPIA.get());
    }
}
