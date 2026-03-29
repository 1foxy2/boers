package net.foxy.boers.particle.spark;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class SparkParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprites;

    public SparkParticleProvider(SpriteSet sprites) {
        this.sprites = sprites;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                   double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed) {
        Vec3 velocity = new Vec3(xSpeed, ySpeed, zSpeed);
        Direction face = Direction.UP;

        return new SparkParticle(level, x, y, z, velocity, face, sprites);
    }
}
