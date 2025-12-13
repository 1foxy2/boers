package net.foxy.boers.client;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BoersClientConfig {
    public static final BoersClientConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    public final ForgeConfigSpec.BooleanValue BREAKING_SOUNDS;
    public final ForgeConfigSpec.IntValue PARTICLE_COUNT;
    public final ForgeConfigSpec.IntValue PARTICLE_DENSITY;
    public final ForgeConfigSpec.DoubleValue PARTICLE_SIZE;
    public final ForgeConfigSpec.IntValue MAX_BOER_HEATING;

    private BoersClientConfig(ForgeConfigSpec.Builder builder) {
        BREAKING_SOUNDS = builder.comment("wip, will change").define("breaking_sounds", false);
        PARTICLE_COUNT = builder.comment("How many particles to spawn").defineInRange("particle_count", 5, 0, Integer.MAX_VALUE);
        PARTICLE_DENSITY = builder.comment("How dense are particles").defineInRange("particle_density", 35, 0, Integer.MAX_VALUE);
        PARTICLE_SIZE = builder.comment("How big are particles").defineInRange("particle_size", 0.10D, 0, Integer.MAX_VALUE);
        MAX_BOER_HEATING = builder.comment("How red can boer head become").defineInRange("max_boer_heating", 95, 0, 255);
    }

    static {
        Pair<BoersClientConfig, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(BoersClientConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
