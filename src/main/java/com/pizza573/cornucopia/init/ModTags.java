package com.pizza573.cornucopia.init;

import com.pizza573.cornucopia.Cornucopia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

// undo, from farmer's delight
public class ModTags
{
    // Items which are compatible with the Backstabbing enchantment. Populated by #tools/knives.
    public static final TagKey<Item> CORNUCOPIA_ENCHANTABLE = modItemTag("enchantable/cornucopia");

    private static TagKey<Item> modItemTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Cornucopia.MOD_ID, path));
    }
}
