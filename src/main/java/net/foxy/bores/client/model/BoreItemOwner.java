package net.foxy.bores.client.model;

import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record BoreItemOwner(boolean isUsed, int usedFor) implements ItemOwner {
    @Override
    public Level level() {
        return null;
    }

    @Override
    public Vec3 position() {
        return Vec3.ZERO;
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return 0;
    }
}
