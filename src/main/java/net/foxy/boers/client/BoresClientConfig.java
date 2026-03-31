package net.foxy.boers.client;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BoresClientConfig {
    public static final BoresClientConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    public final ForgeConfigSpec.BooleanValue BREAKING_SOUNDS;
    public final ForgeConfigSpec.IntValue PARTICLE_COUNT;
    public final ForgeConfigSpec.IntValue PARTICLE_DENSITY;
    public final ForgeConfigSpec.DoubleValue PARTICLE_SIZE;
    public final ForgeConfigSpec.IntValue MAX_BORE_HEATING;
    public final ForgeConfigSpec.BooleanValue BREAK_WITH_USE_KEY;

    private BoresClientConfig(ForgeConfigSpec.Builder builder) {
        BREAKING_SOUNDS = builder.comment("wip, will change").define("breaking_sounds", false);
        PARTICLE_COUNT = builder.comment("How many particles to spawn").defineInRange("particle_count", 5, 0, Integer.MAX_VALUE);
        PARTICLE_DENSITY = builder.comment("How dense are particles").defineInRange("particle_density", 35, 0, Integer.MAX_VALUE);
        PARTICLE_SIZE = builder.comment("How big are particles").defineInRange("particle_size", 0.10D, 0, Integer.MAX_VALUE);
        MAX_BORE_HEATING = builder.comment("How red can bore head become").defineInRange("max_bore_heating", 95, 0, 255);
        BREAK_WITH_USE_KEY = builder.comment("With this bore will also mine if you hold use key").define("break_with_use_key", false);
    }

    static {
        Pair<BoresClientConfig, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(BoresClientConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
