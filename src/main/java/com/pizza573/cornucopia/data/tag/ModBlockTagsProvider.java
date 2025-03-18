package com.pizza573.cornucopia.data.tag;

import com.pizza573.cornucopia.Cornucopia;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;


public class ModBlockTagsProvider extends BlockTagsProvider
{
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, Cornucopia.MOD_ID, existingFileHelper);

    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {

    }
}
