package net.foxy.drills.datagen;

import net.foxy.drills.DrillsMod;
import net.foxy.drills.datagen.loot.ChestLootSubProvider;
import net.foxy.drills.datagen.loot.ModGLM;
import net.foxy.drills.datagen.loot.ModLootTablesProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = DrillsMod.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

//        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput));

        //generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));

        ModBlockTagsProvider blockTagsProvider = generator.addProvider(event.includeServer(),
                new ModBlockTagsProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagsProvider(packOutput,
                lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new DefaultDrillHeadsProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModGLM(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModLootTablesProvider(packOutput, List.of(new LootTableProvider.SubProviderEntry(
                ChestLootSubProvider::new,
                LootContextParamSets.CHEST
        )), lookupProvider));
        /*generator.addProvider(event.includeServer(), new ModCuriosProvider(
                packOutput, existingFileHelper, lookupProvider));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(
                packOutput));*/
//
//        generator.addProvider(event.includeServer(), new ModWorldGenProvider(packOutput, lookupProvider));
    }
}
