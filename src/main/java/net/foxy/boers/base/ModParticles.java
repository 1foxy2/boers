package net.foxy.boers.base;

import net.foxy.boers.BoresMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, BoresMod.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK_PARTICLE = PARTICLE_TYPES.register(
            "spark_particle",
            () -> new SimpleParticleType(false)
    );
}
