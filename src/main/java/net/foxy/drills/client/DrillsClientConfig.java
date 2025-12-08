package net.foxy.drills.client;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DrillsClientConfig {
    public static final DrillsClientConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.BooleanValue BREAKING_SOUNDS;

    private DrillsClientConfig(ModConfigSpec.Builder builder) {
        BREAKING_SOUNDS = builder.comment("wip, will change").define("breaking_sounds", false);
    }

    static {
        Pair<DrillsClientConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(DrillsClientConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
