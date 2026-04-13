package net.foxy.bores.particle.spark;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;

public class SparkParticleRenderGroup extends ParticleGroup<SparkParticle> {
    public static final ParticleRenderType TYPE = new ParticleRenderType("BORES:SINGLE_QUADS");
    final QuadParticleRenderState particleTypeRenderState = new SparkParticleRenderState();

    public SparkParticleRenderGroup(ParticleEngine engine) {
        super(engine);
    }

    @Override
    public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTickTime) {
        for (SparkParticle particle : this.particles) {
            if (frustum.pointInFrustum(particle.getX(), particle.getY(), particle.getZ())) {
                try {
                    particle.extract(this.particleTypeRenderState, camera, partialTickTime);
                } catch (Throwable var9) {
                    CrashReport report = CrashReport.forThrowable(var9, "Rendering Particle");
                    CrashReportCategory category = report.addCategory("Particle being rendered");
                    category.setDetail("Particle", particle::toString);
                    throw new ReportedException(report);
                }
            }
        }

        return this.particleTypeRenderState;
    }
}
