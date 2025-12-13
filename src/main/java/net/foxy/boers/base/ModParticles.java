package net.foxy.boers.base;

import net.foxy.boers.BoersMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BoersMod.MODID);

    public static final RegistryObject<SimpleParticleType> SPARK_PARTICLE = PARTICLE_TYPES.register(
            "spark_particle",
            () -> new SimpleParticleType(false)
    );
}
