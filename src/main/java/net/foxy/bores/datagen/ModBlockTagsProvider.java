package net.foxy.bores.datagen;

import net.foxy.bores.BoresMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, BoresMod.MODID);
    }


    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
