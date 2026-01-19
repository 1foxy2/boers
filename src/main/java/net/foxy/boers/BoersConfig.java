package net.foxy.boers;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BoersConfig {
    public static final BoersConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.BooleanValue ENCHANTABLE_BOER_HEAD;

    private BoersConfig(ModConfigSpec.Builder builder) {
        ENCHANTABLE_BOER_HEAD = builder.comment("if enabled you can now enchant boer heads").define("enchantable_boer_head", false);
    }

    static {
        Pair<BoersConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(BoersConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
