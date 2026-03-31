package net.foxy.boers.client;

import net.foxy.boers.event.ModClientEvents;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class BoreSoundInstance extends EntityBoundSoundInstance {

    public BoreSoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, LivingEntity entity, long seed) {
        super(soundEvent, source, volume, pitch, entity, seed);
        looping = true;
    }

    public void remove() {
        this.stop();
    }

    @Override
    public boolean canPlaySound() {
        if (ModClientEvents.usingProgress < 9) {
            return false;
        }
        return super.canPlaySound();
    }
}
