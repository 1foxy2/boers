package net.foxy.boers;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BoresConfig {
    public static final BoresConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    public final ForgeConfigSpec.BooleanValue ENCHANTABLE_BORE_HEAD;

    private BoresConfig(ForgeConfigSpec.Builder builder) {
        ENCHANTABLE_BORE_HEAD = builder.comment("if enabled you can now enchant bore heads").define("enchantable_bore_head", false);
    }

    static {
        Pair<BoresConfig, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(BoresConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
