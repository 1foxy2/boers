package net.foxy.boers.data;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.foxy.boers.BoersConfig;
import net.neoforged.neoforge.common.conditions.ICondition;

public class ConfigEnchantableCondition implements ICondition {
    public static final MapCodec<ConfigEnchantableCondition> CODEC =
            MapCodec.unit(new ConfigEnchantableCondition());

    @Override
    public boolean test(IContext context) {
        LogUtils.getLogger().warn("testru");
        return BoersConfig.CONFIG.ENCHANTABLE_BOER_HEAD.get();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
