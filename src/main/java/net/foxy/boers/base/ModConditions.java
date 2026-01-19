package net.foxy.boers.base;

import com.mojang.serialization.MapCodec;
import net.foxy.boers.BoersMod;
import net.foxy.boers.data.ConfigEnchantableCondition;
import net.neoforged.neoforge.common.conditions.AndCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModConditions {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, BoersMod.MODID);
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<ConfigEnchantableCondition>> ENCHANTABLE = CONDITION_CODECS.register("enchantable", () -> ConfigEnchantableCondition.CODEC);
}
