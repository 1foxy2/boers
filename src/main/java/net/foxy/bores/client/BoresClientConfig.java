package net.foxy.bores.client;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BoresClientConfig {
    public static final BoresClientConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.BooleanValue BREAKING_SOUNDS;
    public final ModConfigSpec.IntValue PARTICLE_COUNT;
    public final ModConfigSpec.IntValue PARTICLE_DENSITY;
    public final ModConfigSpec.DoubleValue PARTICLE_SIZE;
    public final ModConfigSpec.IntValue MAX_BOER_HEATING;
    public final ModConfigSpec.BooleanValue BREAK_WITH_USE_KEY;

    private BoresClientConfig(ModConfigSpec.Builder builder) {
        BREAKING_SOUNDS = builder.comment("wip, will change").define("breaking_sounds", false);
        PARTICLE_COUNT = builder.comment("How many particles to spawn").defineInRange("particle_count", 5, 0, Integer.MAX_VALUE);
        PARTICLE_DENSITY = builder.comment("How dense are particles").defineInRange("particle_density", 35, 0, Integer.MAX_VALUE);
        PARTICLE_SIZE = builder.comment("How big are particles").defineInRange("particle_size", 0.10D, 0, Integer.MAX_VALUE);
        MAX_BOER_HEATING = builder.comment("How red can boer head become").defineInRange("max_boer_heating", 95, 0, 255);
        BREAK_WITH_USE_KEY = builder.comment("With this boer will also mine if you hold use key").define("break_with_use_key", false);
    }

    static {
        Pair<BoresClientConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(BoresClientConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
