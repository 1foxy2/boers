package net.foxy.bores.particle.spark;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.feature.ParticleFeatureRenderer;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SparkParticleRenderState extends QuadParticleRenderState {
    private final Map<SingleQuadParticle.Layer, SparkParticleRenderState.Storage> particles = new HashMap<>();

    public void add(
            SingleQuadParticle.Layer layer,
            float x,
            float y,
            float z,
            float xd,
            float yd,
            float zd,
            float xRot,
            float yRot,
            float zRot,
            float wRot,
            float scale,
            float u0,
            float u1,
            float v0,
            float v1,
            int color,
            int lightCoords
    ) {
        this.particles
                .computeIfAbsent(layer, ignored -> new SparkParticleRenderState.Storage())
                .add(x, y, z, xd, yd, zd, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords);
        this.particleCount++;
    }

    @Override
    public void clear() {
        super.clear();
        this.particles.values().forEach(SparkParticleRenderState.Storage::clear);
    }

    @Override
    public QuadParticleRenderState.@Nullable PreparedBuffers prepare(ParticleFeatureRenderer.ParticleBufferCache cachedBuffer, boolean translucent) {
        if (this.isEmpty()) {
            return null;
        } else {
            int vertexCount = this.particleCount * 4;

            Object var14;
            try (ByteBufferBuilder builder = ByteBufferBuilder.exactlySized(vertexCount * DefaultVertexFormat.PARTICLE.getVertexSize())) {
                BufferBuilder bufferBuilder = new BufferBuilder(builder, VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                Map<SingleQuadParticle.Layer, PreparedLayer> preparedLayers = new HashMap<>();
                int offset = 0;

                for (Map.Entry<SingleQuadParticle.Layer, SparkParticleRenderState.Storage> entry : this.particles.entrySet()) {
                    if (entry.getKey().translucent() == translucent) {
                        entry.getValue()
                                .forEachParticle(
                                        (x, y, z, xd, yd, zd, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords) -> this.renderRotatedQuad(
                                                bufferBuilder, x, y, z, xd, yd, zd, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords
                                        )
                                );
                        if (entry.getValue().count() > 0) {
                            preparedLayers.put(entry.getKey(), new QuadParticleRenderState.PreparedLayer(offset, entry.getValue().count() * 6));
                        }

                        offset += entry.getValue().count() * 4;
                    }
                }

                MeshData mesh = bufferBuilder.build();
                if (mesh != null) {
                    cachedBuffer.write(mesh.vertexBuffer());
                    RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS).getBuffer(mesh.drawState().indexCount());
                    GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                            .writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());
                    return new QuadParticleRenderState.PreparedBuffers(mesh.drawState().indexCount(), dynamicTransforms, preparedLayers);
                }

                var14 = null;
            }

            return (QuadParticleRenderState.PreparedBuffers)var14;
        }
    }

    protected void renderRotatedQuad(
            VertexConsumer buffer,
            float x,
            float y,
            float z,
            float xd,
            float yd,
            float zd,
            float xRot,
            float yRot,
            float zRot,
            float wRot,
            float quadSize,
            float u0,
            float u1,
            float v0,
            float v1,
            int color,
            int light
    ) {

        Vec3 velocity = new Vec3(xd, yd, zd);
        float speed = (float)velocity.length();

        if (speed < 0.01F) {
            renderRotatedQuad(buffer, x, y, z, xRot, yRot, zRot, wRot, quadSize, u0, u1, v0, v1, color, light);
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

        float baseStretch = 2.0F;
        float speedStretch = Math.min(speed * 5.0F, 3.0F);
        float totalStretch = baseStretch + speedStretch;

        float halfWidth = quadSize * 0.25F;

        float halfLength = quadSize * totalStretch * 0.5F;

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
                .setColor(color)
                .setLight(light);

        buffer.addVertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .setUv(u0, v0)
                .setColor(color)
                .setLight(light);

        buffer.addVertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .setUv(u1, v0)
                .setColor(color)
                .setLight(light);

        buffer.addVertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .setUv(u1, v1)
                .setColor(color)
                .setLight(light);
    }

    @FunctionalInterface
    public interface ParticleConsumer {
        void consume(
                final float x,
                final float y,
                final float z,
                final float xd,
                final float yd,
                final float zd,
                final float xRot,
                final float yRot,
                final float zRot,
                final float wRot,
                final float scale,
                final float u0,
                final float u1,
                final float v0,
                final float v1,
                final int color,
                final int lightCoords
        );
    }

    private static class Storage {
        private int capacity = 1024;
        private float[] floatValues = new float[12288];
        private int[] intValues = new int[2048];
        private int currentParticleIndex;

        public void add(
                float x,
                float y,
                float z,
                float xd,
                float yd,
                float zd,
                float xRot,
                float yRot,
                float zRot,
                float wRot,
                float scale,
                float u0,
                float u1,
                float v0,
                float v1,
                int color,
                int lightCoords
        ) {
            if (this.currentParticleIndex >= this.capacity) {
                this.grow();
            }

            int index = this.currentParticleIndex * 15;
            this.floatValues[index++] = x;
            this.floatValues[index++] = y;
            this.floatValues[index++] = z;
            this.floatValues[index++] = xd;
            this.floatValues[index++] = yd;
            this.floatValues[index++] = zd;
            this.floatValues[index++] = xRot;
            this.floatValues[index++] = yRot;
            this.floatValues[index++] = zRot;
            this.floatValues[index++] = wRot;
            this.floatValues[index++] = scale;
            this.floatValues[index++] = u0;
            this.floatValues[index++] = u1;
            this.floatValues[index++] = v0;
            this.floatValues[index] = v1;
            index = this.currentParticleIndex * 2;
            this.intValues[index++] = color;
            this.intValues[index] = lightCoords;
            this.currentParticleIndex++;
        }

        public void forEachParticle(SparkParticleRenderState.ParticleConsumer consumer) {
            for (int particleIndex = 0; particleIndex < this.currentParticleIndex; particleIndex++) {
                int floatIndex = particleIndex * 15;
                int intIndex = particleIndex * 2;
                consumer.consume(
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex],
                        this.intValues[intIndex++],
                        this.intValues[intIndex]
                );
            }
        }

        public void clear() {
            this.currentParticleIndex = 0;
        }

        private void grow() {
            this.capacity *= 2;
            this.floatValues = Arrays.copyOf(this.floatValues, this.capacity * 12);
            this.intValues = Arrays.copyOf(this.intValues, this.capacity * 2);
        }

        public int count() {
            return this.currentParticleIndex;
        }
    }
}
