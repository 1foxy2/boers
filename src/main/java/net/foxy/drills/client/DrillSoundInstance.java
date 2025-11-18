package net.foxy.drills.client;

import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DrillSoundInstance extends EntityBoundSoundInstance {
    private final LivingEntity entity2;

    public DrillSoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, LivingEntity entity, long seed) {
        super(soundEvent, source, volume, pitch, entity, seed);
        entity2 = entity;
    }

    @Override
    public void tick() {
        if (!entity2.isUsingItem()) {
            stop();
        }
        super.tick();
    }
}
