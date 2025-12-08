package net.foxy.boers.client;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BoersClientConfig {
    public static final BoersClientConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.BooleanValue BREAKING_SOUNDS;
    public final ModConfigSpec.IntValue PARTICLE_COUNT;
    public final ModConfigSpec.IntValue PARTICLE_DENSITY;
    public final ModConfigSpec.DoubleValue PARTICLE_SIZE;

    private BoersClientConfig(ModConfigSpec.Builder builder) {
        BREAKING_SOUNDS = builder.comment("wip, will change").define("breaking_sounds", false);
        PARTICLE_COUNT = builder.comment("How many particles to spawn").defineInRange("particle_count", 5, 0, Integer.MAX_VALUE);
        PARTICLE_DENSITY = builder.comment("How dense are particles").defineInRange("particle_density", 35, 0, Integer.MAX_VALUE);
        PARTICLE_SIZE = builder.comment("How big are particles").defineInRange("particle_size", 0.10D, 0, Integer.MAX_VALUE);
    }

    static {
        Pair<BoersClientConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(BoersClientConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
