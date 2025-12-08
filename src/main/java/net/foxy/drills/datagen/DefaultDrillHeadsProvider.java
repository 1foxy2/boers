package net.foxy.drills.datagen;

import net.foxy.drills.data.DrillHead;
import net.foxy.drills.data.DrillOutput;
import net.foxy.drills.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class DefaultDrillHeadsProvider {
    public DefaultDrillHeadsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    }

    protected void buildDrills(DrillOutput output) {
        create(output, "default", 1, 100, BlockTags.INCORRECT_FOR_IRON_TOOL);
        create(output, "copper", 10, 95, BlockTags.INCORRECT_FOR_STONE_TOOL);
        create(output, "iron", 12, 125, BlockTags.INCORRECT_FOR_IRON_TOOL);
        create(output, "diamond", 16, 800, BlockTags.INCORRECT_FOR_DIAMOND_TOOL);
        create(output, "golden", 24, 16, BlockTags.INCORRECT_FOR_GOLD_TOOL);
        create(output, "netherite", 18, 1000, BlockTags.INCORRECT_FOR_NETHERITE_TOOL);
    }
    private static void create(DrillOutput output, String id, float miningSpeed, int durability, TagKey<Block> canMine) {
        create(output, id, "item/drill/" + id + "_drill_head", miningSpeed, durability, canMine);
    }

    private static void create(DrillOutput output, String id, String texture, float miningSpeed, int durability, TagKey<Block> canMine) {
        output.accept(Utils.rl(id), new DrillHead(Utils.rl(texture), miningSpeed, durability, canMine));
    }
}
