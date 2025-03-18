package com.pizza573.cornucopia.data.advancement;

import com.pizza573.cornucopia.common.registry.ModItems;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.UsingItemTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider
{
    // Parameters can be obtained from GatherDataEvent.
    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, existingFileHelper, List.of(new ModAdvancementGenerator()));
    }

    private static final class ModAdvancementGenerator implements AdvancementProvider.AdvancementGenerator
    {
        @Override
        public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> saver, @NotNull ExistingFileHelper existingFileHelper)
        {
            // yummy
            Advancement.Builder builder1 = Advancement.Builder.advancement();
            // 添加父节点
            builder1.parent(AdvancementSubProvider.createPlaceholder("minecraft:husbandry/root"));
            builder1.display(
                    new ItemStack(Items.GOAT_HORN),
                    Component.translatable("advancements.husbandry.yummy.title"),
                    Component.translatable("advancements.husbandry.yummy.description"),
                    null,
                    AdvancementType.TASK,
                    true,
                    true,
                    true
            );
            // 添加奖励
            builder1.rewards(
                    AdvancementRewards.Builder.experience(0)
                    .addLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("cornucopia", "main/one_goat_horn")))
            );
            // 添加准则
            builder1.addCriterion("consumed_item", ConsumeItemTrigger.TriggerInstance.usedItem());
            builder1.save(saver, ResourceLocation.fromNamespaceAndPath("cornucopia", "main/yummy"), existingFileHelper);

            // using cornucopia
            Advancement.Builder builder2 = Advancement.Builder.advancement();
            builder2.parent(AdvancementSubProvider.createPlaceholder("cornucopia:main/yummy"));
            builder2.display(
                    new ItemStack(ModItems.CREATIVE_TAB_DISPLAY.get()),
                    Component.translatable("advancements.husbandry.using_cornucopia.title"),
                    Component.translatable("advancements.husbandry.using_cornucopia.description"),
                    null,
                    AdvancementType.TASK,
                    true,
                    true,
                    true
            );
            builder2.addCriterion("using_cornucopia", UsingItemTrigger.TriggerInstance.lookingAt(EntityPredicate.Builder.entity(), ItemPredicate.Builder.item().of(ModItems.CORNUCOPIA.get())));
            builder2.requirements(AdvancementRequirements.allOf(List.of("using_cornucopia")));
            builder2.rewards(
                    AdvancementRewards.Builder.experience(0)
                            .addLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("cornucopia", "main/three_golden_apple")))
            );
            builder2.save(saver, ResourceLocation.fromNamespaceAndPath("cornucopia", "main/using_cornucopia"), existingFileHelper);
        }
    }
}
