package net.foxy.bores.particle.spark;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxy.bores.client.BoresClientConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SparkParticle extends SingleQuadParticle {
    private final SpriteSet spriteSet;
    private final float initialSize;

    protected SparkParticle(ClientLevel level, double x, double y, double z,
                            Vec3 velocity, Direction blockFace, SpriteSet spriteSet) {
        super(level, x, y, z, spriteSet.first());

        this.spriteSet = spriteSet;

        this.xd = velocity.x;
        this.yd = velocity.y;
        this.zd = velocity.z;

        this.gravity = 0.6F;
        this.friction = 0.96F;
        this.lifetime = 15 + random.nextInt(10);

        this.initialSize = (float) (BoresClientConfig.CONFIG.PARTICLE_SIZE.get() * (0.3F + random.nextFloat()));
        this.quadSize = initialSize;

        float colorVar = random.nextFloat() * 0.2F;
        this.rCol = 1.0F;
        this.gCol = 0.8F + colorVar;
        this.bCol = 0.3F + colorVar * 0.5F;
        this.alpha = 1.0F;

        this.hasPhysics = true;
    }

    @Override
    public ParticleRenderType getGroup() {
        return SparkParticleRenderGroup.TYPE;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    protected void extractRotatedQuad(QuadParticleRenderState particleTypeRenderState, Quaternionf rotation, float x, float y, float z, float partialTickTime) {
        if (particleTypeRenderState instanceof SparkParticleRenderState sparkParticleRenderState) {
            sparkParticleRenderState.add(
                    this.getLayer(),
                    x,
                    y,
                    z,
                    (float) xd,
                    (float) yd,
                    (float) zd,
                    rotation.x,
                    rotation.y,
                    rotation.z,
                    rotation.w,
                    this.getQuadSize(partialTickTime),
                    this.getU0(),
                    this.getU1(),
                    this.getV0(),
                    this.getV1(),
                    ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol),
                    this.getLightCoords(partialTickTime)
            );
        } else {
            super.extractRotatedQuad(particleTypeRenderState, rotation, x, y, z, partialTickTime);
        }
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float lifeProgress = (float)this.age / (float)this.lifetime;

        this.quadSize = initialSize * (1.0F - lifeProgress);

        this.alpha = 1.0F - lifeProgress * 0.9F;

        float brightness = 1.0F - lifeProgress * 0.5F;
        this.rCol = brightness;
        this.gCol = (0.7F - lifeProgress * 0.4F) * brightness;
        this.bCol = (0.2F - lifeProgress * 0.2F) * brightness;

        this.yd -= 0.03D * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;

        if (this.onGround) {
            if (Math.abs(this.yd) > 0.01) {
                this.yd *= -0.3F;
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            } else {
                this.xd *= 0.5F;
                this.zd *= 0.5F;
            }
        }

        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    protected int getLightCoords(float a) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    public static Vec3 generateConeVelocity(Vec3 contactPoint, Vec3 playerEye, float speed) {
        Vec3 toPlayer = playerEye.subtract(contactPoint).normalize();

        double coneAngleRad = Math.toRadians(BoresClientConfig.CONFIG.PARTICLE_DENSITY.get());
        double randomAngle = Math.random() * 2 * Math.PI;
        double randomRadius = Math.random() * Math.tan(coneAngleRad);

        Vec3 perpendicular1;
        if (Math.abs(toPlayer.y) < 0.9) {
            perpendicular1 = toPlayer.cross(new Vec3(0, 1, 0)).normalize();
        } else {
            perpendicular1 = toPlayer.cross(new Vec3(1, 0, 0)).normalize();
        }
        Vec3 perpendicular2 = toPlayer.cross(perpendicular1).normalize();

        Vec3 offset = perpendicular1.scale(Math.cos(randomAngle) * randomRadius)
                .add(perpendicular2.scale(Math.sin(randomAngle) * randomRadius));

        Vec3 finalDir = toPlayer.add(offset).normalize();

        float randomSpeed = speed * (0.4F + (float)Math.random() * 0.6F);

        return finalDir.scale(randomSpeed);
    }
}