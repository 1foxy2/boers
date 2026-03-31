package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BoresMod.MODID);

    public static final RegistryObject<SoundEvent> HEAD_EQUIP = SOUND_EVENTS.register(
            "head_equip", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(BoresMod.MODID, "head_equip")));
    public static final RegistryObject<SoundEvent> HEAD_UNEQUIP = SOUND_EVENTS.register(
            "head_unequip", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(BoresMod.MODID, "head_unequip")));
    public static final RegistryObject<SoundEvent> STONE = SOUND_EVENTS.register(
            "stone", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(BoresMod.MODID, "stone")));
    public static final RegistryObject<SoundEvent> AIR = SOUND_EVENTS.register(
            "air", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(BoresMod.MODID, "air")));

}
