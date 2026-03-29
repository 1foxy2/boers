package net.foxy.boers.base;

import net.foxy.boers.BoresMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BoresMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> HEAD_EQUIP = SOUND_EVENTS.register(
            "head_equip", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(BoresMod.MODID, "head_equip")));
    public static final DeferredHolder<SoundEvent, SoundEvent> HEAD_UNEQUIP = SOUND_EVENTS.register(
            "head_unequip", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(BoresMod.MODID, "head_unequip")));
    public static final DeferredHolder<SoundEvent, SoundEvent> STONE = SOUND_EVENTS.register(
            "stone", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(BoresMod.MODID, "stone")));
    public static final DeferredHolder<SoundEvent, SoundEvent> AIR = SOUND_EVENTS.register(
            "air", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(BoresMod.MODID, "air")));

}
