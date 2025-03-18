package com.pizza573.cornucopia.common.tag;

import com.pizza573.cornucopia.Cornucopia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags
{
    // item tag
    public static final TagKey<Item> CORNUCOPIA_ENCHANTABLE = modItemTag("enchantable/cornucopia");

    // Knife items for game logic.
    public static final TagKey<Item> CORNUCOPIA = modItemTag("tools/cornucopia");

    private static TagKey<Item> modItemTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Cornucopia.MOD_ID, path));
    }
}
