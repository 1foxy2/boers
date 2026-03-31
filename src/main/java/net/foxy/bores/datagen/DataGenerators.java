package net.foxy.bores.datagen;

import net.foxy.bores.BoresMod;
import net.foxy.bores.base.ModRegistries;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.datagen.loot.ChestLootSubProvider;
import net.foxy.bores.datagen.loot.ModGLM;
import net.foxy.bores.datagen.loot.ModLootTablesProvider;
import net.foxy.bores.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = BoresMod.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        event.createDatapackRegistryObjects(
                new RegistrySetBuilder().add(
                        ModRegistries.BORE_HEAD, bootstrap -> {
                            bootstrap.register(
                                    ModRegistries.COPPER,
                                    create("copper", 10, 95, BlockTags.INCORRECT_FOR_STONE_TOOL)
                            );
                            bootstrap.register(
                                    ModRegistries.IRON,
                                    create("iron", 12, 125, BlockTags.INCORRECT_FOR_IRON_TOOL)
                            );
                            bootstrap.register(
                                    ModRegistries.DIAMOND,
                                    create("diamond", 16, 800, BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                            );
                            bootstrap.register(
                                    ModRegistries.GOLDEN,
                                    create("golden", 24, 16, BlockTags.INCORRECT_FOR_GOLD_TOOL)
                            );
                            bootstrap.register(
                                    ModRegistries.NETHERITE,
                                    create("netherite", 18, 1000, BlockTags.INCORRECT_FOR_NETHERITE_TOOL)
                            );
                        }
                ),
                conditions -> {},
                Set.of(BoresMod.MODID)
        );

        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));

        ModBlockTagsProvider blockTagsProvider = generator.addProvider(event.includeServer(),
                new ModBlockTagsProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagsProvider(packOutput,
                lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModGLM(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModLootTablesProvider(packOutput, List.of(new LootTableProvider.SubProviderEntry(
                ChestLootSubProvider::new,
                LootContextParamSets.CHEST
        )), lookupProvider));
    }

    private static BoreHead create(String id, float miningSpeed, int durability, TagKey<Block> canMine) {
        return new BoreHead(Utils.rl("item/bore/" + id + "_bore_head"), miningSpeed, miningSpeed * 3, 0.1f, durability, canMine);
    }

    private static BoreHead create(String id, float miningSpeed, int durability, TagKey<Block> canMine, Vec3i radius) {
        return new BoreHead(Utils.rl("item/bore/" + id + "_bore_head"), miningSpeed, miningSpeed * 3, 0.1f, durability, canMine, radius);
    }
}
