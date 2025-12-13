package net.foxy.boers.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.foxy.boers.BoersMod;
import net.foxy.boers.datagen.loot.BoerLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModGlobalLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
            ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BoersMod.MODID);

    public static final RegistryObject<Codec<BoerLootModifier>> BOER = LOOT_MODIFIERS.register("example_codec", () -> BoerLootModifier.CODEC);
}
