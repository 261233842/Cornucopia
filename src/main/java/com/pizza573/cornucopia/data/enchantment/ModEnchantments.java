package com.pizza573.cornucopia.data.enchantment;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.common.registry.ModDataComponents;
import com.pizza573.cornucopia.common.registry.ModItems;
import com.pizza573.cornucopia.common.tag.ModTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;

public class ModEnchantments
{
    // 定义一个附魔资源键
    public static final ResourceKey<Enchantment> CAPACITY = key("capacity");

    public static void bootstrap(BootstrapContext<Enchantment> context)
    {
        // 获取各种注册表的持有者获取器
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        register( // 注册自定义附魔
                context,
                CAPACITY,
                Enchantment.enchantment(
                        Enchantment.definition(
                                items.getOrThrow(ModTags.CORNUCOPIA),
                                5,
                                2,
                                Enchantment.dynamicCost(15, 9),
                                Enchantment.dynamicCost(50, 8),
                                4,
                                EquipmentSlotGroup.MAINHAND
                        )
                ).withEffect(
                        ModDataComponents.CAPACITY.get(),
                        new AddValue(LevelBasedValue.perLevel(64.0F, 64.0F))
                )
        );
    }

    // 注册附魔的方法
    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder)
    {
        context.register(key, builder.build(key.location()));
    }

    // 创建附魔资源键的方法
    private static ResourceKey<Enchantment> key(String name)
    {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Cornucopia.MOD_ID, name));
    }
}