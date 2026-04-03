package net.foxy.bores.particle.spark;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SparkParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprites;

    public SparkParticleProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level,
                                             double x, double y, double z,
                                             double xAux, double yAux, double zAux, RandomSource random) {
        Vec3 velocity = new Vec3(xAux, yAux, zAux);
        Direction face = Direction.UP;

        return new SparkParticle(level, x, y, z, velocity, face, sprites);
    }
}
