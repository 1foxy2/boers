package net.foxy.bores.datagen;

import net.foxy.bores.BoresMod;
import net.foxy.bores.base.ModRegistries;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.datagen.loot.ChestLootSubProvider;
import net.foxy.bores.datagen.loot.ModGLM;
import net.foxy.bores.datagen.loot.ModLootTablesProvider;
import net.foxy.bores.util.Utils;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = BoresMod.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        event.createDatapackRegistryObjects(
                new RegistrySetBuilder().add(
                        ModRegistries.BORE_HEAD, bootstrap -> {
                            HolderGetter<Block> blocks = bootstrap.lookup(Registries.BLOCK);
                            bootstrap.register(
                                    ModRegistries.COPPER,
                                    create("copper", 10, 190, blocks, BlockTags.INCORRECT_FOR_STONE_TOOL)
                            );
                            bootstrap.register(
                                    ModRegistries.IRON,
                                    create("iron", 12, 250, blocks, BlockTags.INCORRECT_FOR_IRON_TOOL)
                            );
                            bootstrap.register(
                                    ModRegistries.DIAMOND,
                                    create("diamond", 16, 1561, blocks, BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                            );
                            bootstrap.register(
                                    ModRegistries.GOLDEN,
                                    create("golden", 24, 32, blocks, BlockTags.INCORRECT_FOR_GOLD_TOOL)
                            );
                            bootstrap.register(
                                    ModRegistries.NETHERITE,
                                    create("netherite", 18, 2031, blocks, BlockTags.INCORRECT_FOR_NETHERITE_TOOL)
                            );
                        }
                ),
                conditions -> {},
                Set.of(BoresMod.MODID)
        );

        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        //event.addProvider(new ModItemModelProvider(packOutput));

        ModBlockTagsProvider blockTagsProvider = event.addProvider(new ModBlockTagsProvider(packOutput, lookupProvider));
        event.addProvider(new ModItemTagsProvider(packOutput,
                lookupProvider, blockTagsProvider.contentsGetter()));
        event.addProvider(new ModRecipeProvider.Runner(packOutput, lookupProvider));
        event.addProvider(new ModGLM(packOutput, lookupProvider));
        event.addProvider(new ModLootTablesProvider(packOutput, List.of(new LootTableProvider.SubProviderEntry(
                ChestLootSubProvider::new,
                LootContextParamSets.CHEST
        )), lookupProvider));
    }

    private static BoreHead create(String id, float miningSpeed, int durability, HolderGetter<Block> blocks, TagKey<Block> canMine) {
        return new BoreHead(Utils.rl("item/bore/" + id + "_bore_head"), miningSpeed, miningSpeed * 3, 0.1f, durability, blocks, canMine);
    }

    private static BoreHead create(String id, float miningSpeed, int durability, HolderGetter<Block> blocks, TagKey<Block> canMine, Vec3i radius) {
        return new BoreHead(Utils.rl("item/bore/" + id + "_bore_head"), miningSpeed, miningSpeed * 3, 0.1f, durability, blocks, canMine, radius);
    }
}
