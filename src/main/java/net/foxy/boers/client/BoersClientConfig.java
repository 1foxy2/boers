package net.foxy.boers.client;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BoersClientConfig {
    public static final BoersClientConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    public final ForgeConfigSpec.BooleanValue BREAKING_SOUNDS;

    private BoersClientConfig(ForgeConfigSpec.Builder builder) {
        BREAKING_SOUNDS = builder.comment("wip, will change").define("breaking_sounds", false);
    }

    static {
        Pair<BoersClientConfig, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(BoersClientConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
