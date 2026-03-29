package net.foxy.boers.datagen.loot;

import net.foxy.boers.base.ModItems;
import net.foxy.boers.base.ModLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public record ChestLootSubProvider(HolderLookup.Provider registries) implements LootTableSubProvider {


    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(ModLootTables.ABANDONED_MINESHAFT, LootTable.lootTable().withPool(
                LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.BORE)).when(LootItemRandomChanceCondition.randomChance(0.33f))));
    }
}
