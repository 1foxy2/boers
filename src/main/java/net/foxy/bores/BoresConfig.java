package net.foxy.bores;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BoresConfig {
    public static final BoresConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.BooleanValue ENCHANTABLE_BOER_HEAD;

    private BoresConfig(ModConfigSpec.Builder builder) {
        ENCHANTABLE_BOER_HEAD = builder.comment("if enabled you can now enchant boer heads").define("enchantable_boer_head", false);
    }

    static {
        Pair<BoresConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(BoresConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
