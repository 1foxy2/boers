package net.foxy.boers.base;

import net.foxy.boers.data.BoreHead;
import net.foxy.boers.util.Utils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ModRegistries {
    public static final ResourceKey<Registry<BoreHead>> BORE_HEAD = ResourceKey.createRegistryKey(Utils.rl("bore_head"));
    public static final ResourceKey<Registry<BoreHead>> OLD_BOER_HEAD =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("boers", "boer_head"));

    public static final ResourceKey<BoreHead> COPPER = ResourceKey.create(BORE_HEAD, Utils.rl("copper"));
    public static final ResourceKey<BoreHead> IRON = ResourceKey.create(BORE_HEAD, Utils.rl("iron"));
    public static final ResourceKey<BoreHead> DIAMOND = ResourceKey.create(BORE_HEAD, Utils.rl("diamond"));
    public static final ResourceKey<BoreHead> NETHERITE = ResourceKey.create(BORE_HEAD, Utils.rl("netherite"));
    public static final ResourceKey<BoreHead> GOLDEN = ResourceKey.create(BORE_HEAD, Utils.rl("golden"));
}
