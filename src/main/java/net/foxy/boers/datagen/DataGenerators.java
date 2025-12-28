package net.foxy.boers.datagen;

import net.foxy.boers.BoersMod;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.data.BoerHead;
import net.foxy.boers.datagen.loot.ModGLM;
import net.foxy.boers.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = BoersMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(),
                new DatapackBuiltinEntriesProvider(packOutput, lookupProvider,
                        new RegistrySetBuilder().add(
                                ModRegistries.BOER_HEAD, bootstrap -> {
                                    bootstrap.register(
                                            ModRegistries.COPPER,
                                            create("copper", 10, 95, Tiers.STONE)
                                    );
                                    bootstrap.register(
                                            ModRegistries.IRON,
                                            create("iron", 12, 125, Tiers.IRON)
                                    );
                                    bootstrap.register(
                                            ModRegistries.DIAMOND,
                                            create("diamond", 16, 800, Tiers.DIAMOND)
                                    );
                                    bootstrap.register(
                                            ModRegistries.GOLDEN,
                                            create("golden", 24, 16, Tiers.GOLD)
                                    );
                                    bootstrap.register(
                                            ModRegistries.NETHERITE,
                                            create("netherite", 18, 1000, Tiers.NETHERITE)
                                    );
                                }
                        ) ,
                        Set.of(BoersMod.MODID)
                )
        );


        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));

        ModBlockTagsProvider blockTagsProvider = generator.addProvider(event.includeServer(),
                new ModBlockTagsProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagsProvider(packOutput,
                lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), new ModGLM(packOutput));
    }

    private static BoerHead create(String id, float miningSpeed, int durability, Tier canMine) {
        return new BoerHead(Utils.rl("item/boer/" + id + "_boer_head"), miningSpeed, miningSpeed * 3, 0.1f, durability, canMine);
    }

    private static BoerHead create(String id, float miningSpeed, int durability, Tier canMine, Vec3i radius) {
        return new BoerHead(Utils.rl("item/boer/" + id + "_boer_head"), miningSpeed, miningSpeed * 3, 0.1f, durability, canMine, radius);
    }
}
