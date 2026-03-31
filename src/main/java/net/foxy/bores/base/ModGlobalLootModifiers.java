package net.foxy.bores.base;

import com.mojang.serialization.Codec;
import net.foxy.bores.BoresMod;
import net.foxy.bores.datagen.loot.BoerLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModGlobalLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
            ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BoresMod.MODID);

    public static final RegistryObject<Codec<BoerLootModifier>> BORE = LOOT_MODIFIERS.register("example_codec", () -> BoerLootModifier.CODEC);
}
