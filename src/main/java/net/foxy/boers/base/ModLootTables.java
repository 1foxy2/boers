package net.foxy.boers.base;

import net.foxy.boers.util.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class ModLootTables {
    public static final ResourceKey<LootTable> ABANDONED_MINESHAFT = ResourceKey.create(Registries.LOOT_TABLE, Utils.rl("chests/abandoned_mineshaft_modifier"));

}
