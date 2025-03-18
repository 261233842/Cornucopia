package com.pizza573.cornucopia.common.tag;

import com.pizza573.cornucopia.Cornucopia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags
{
    public static final TagKey<Item> CORNUCOPIA = modItemTag("tools/cornucopia"); // Cornucopia items for game logic.

    private static TagKey<Item> modItemTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Cornucopia.MOD_ID, path));
    }
}
