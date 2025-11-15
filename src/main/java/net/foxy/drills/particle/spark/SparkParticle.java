package net.foxy.drills.particle.spark;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class SparkParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private static final float BASE_SIZE = 0.10F;
    private final float initialSize;

    protected SparkParticle(ClientLevel level, double x, double y, double z,
                            Vec3 velocity, Direction blockFace, SpriteSet spriteSet) {
        super(level, x, y, z);

        this.spriteSet = spriteSet;

        this.xd = velocity.x;
        this.yd = velocity.y;
        this.zd = velocity.z;

        this.gravity = 0.6F;
        this.friction = 0.96F;
        this.lifetime = 15 + random.nextInt(10);

        this.initialSize = BASE_SIZE * (0.3F + random.nextFloat());
        this.quadSize = initialSize;

        float colorVar = random.nextFloat() * 0.2F;
        this.rCol = 1.0F;
        this.gCol = 0.8F + colorVar;
        this.bCol = 0.3F + colorVar * 0.5F;
        this.alpha = 1.0F;

        this.pickSprite(spriteSet);
        this.hasPhysics = true;
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
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x);
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y);
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z);

        Vec3 velocity = new Vec3(this.xd, this.yd, this.zd);
        float speed = (float)velocity.length();

        if (speed < 0.01F) {
            super.render(buffer, camera, partialTicks);
            return;
        }

        Vec3 moveDir = velocity.normalize();
        Vec3 toCamera = new Vec3(-x, -y, -z).normalize();
        Vec3 right = moveDir.cross(toCamera);

        if (right.lengthSqr() < 0.001) {
            Vec3 altUp = Math.abs(moveDir.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
            right = moveDir.cross(altUp);
        }
        right = right.normalize();

        Vec3 up = right.cross(moveDir).normalize();

        float baseStretch = 2.0F;
        float speedStretch = Math.min(speed * 5.0F, 3.0F);
        float totalStretch = baseStretch + speedStretch;

        float halfWidth = this.quadSize * 0.25F;

        float halfLength = this.quadSize * totalStretch * 0.5F;

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        int light = LightTexture.FULL_BRIGHT;

        Vector3f[] vertices = new Vector3f[4];

        Vec3 tail = new Vec3(x, y, z).subtract(moveDir.scale(halfLength));
        vertices[0] = new Vector3f(
                (float)(tail.x - right.x * halfWidth),
                (float)(tail.y - right.y * halfWidth),
                (float)(tail.z - right.z * halfWidth)
        );
        vertices[1] = new Vector3f(
                (float)(tail.x + right.x * halfWidth),
                (float)(tail.y + right.y * halfWidth),
                (float)(tail.z + right.z * halfWidth)
        );

        Vec3 head = new Vec3(x, y, z).add(moveDir.scale(halfLength));
        vertices[2] = new Vector3f(
                (float)(head.x + right.x * halfWidth),
                (float)(head.y + right.y * halfWidth),
                (float)(head.z + right.z * halfWidth)
        );
        vertices[3] = new Vector3f(
                (float)(head.x - right.x * halfWidth),
                (float)(head.y - right.y * halfWidth),
                (float)(head.z - right.z * halfWidth)
        );

        buffer.addVertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .setUv(u0, v1)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(light);

        buffer.addVertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .setUv(u0, v0)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(light);

        buffer.addVertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .setUv(u1, v0)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(light);

        buffer.addVertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .setUv(u1, v1)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(light);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    public static Vec3 generateConeVelocity(Vec3 contactPoint, Vec3 playerEye, float speed) {
        Vec3 toPlayer = playerEye.subtract(contactPoint).normalize();

        double coneAngleRad = Math.toRadians(35);
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