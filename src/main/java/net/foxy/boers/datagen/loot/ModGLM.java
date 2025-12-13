package net.foxy.boers.datagen.loot;

import net.foxy.boers.BoersMod;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class ModGLM extends GlobalLootModifierProvider {
    public ModGLM(PackOutput output) {
        super(output, BoersMod.MODID);
    }

    @Override
    protected void start() {
        add(
                "boers_loot_modifier",
                new BoerLootModifier(new LootItemCondition[] {new LootTableIdCondition.Builder(BuiltInLootTables.ABANDONED_MINESHAFT).build()})
        );
    }
}
