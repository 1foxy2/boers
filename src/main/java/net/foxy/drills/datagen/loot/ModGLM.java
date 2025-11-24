package net.foxy.drills.datagen.loot;

import net.foxy.drills.DrillsMod;
import net.foxy.drills.base.ModLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class ModGLM extends GlobalLootModifierProvider {
    public ModGLM(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, DrillsMod.MODID);
    }

    @Override
    protected void start() {
        add(
                "drills_loot_modifier",
                new AddTableLootModifier(new LootItemCondition[] {new LootTableIdCondition.Builder(BuiltInLootTables.ABANDONED_MINESHAFT.location()).build()}, ModLootTables.ABANDONED_MINESHAFT)
        );
    }
}
