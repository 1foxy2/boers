package net.foxy.boers.base;

import net.foxy.boers.data.BoerHead;
import net.foxy.boers.util.Utils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ModRegistries {
    public static final ResourceKey<Registry<BoerHead>> BOER_HEAD = ResourceKey.createRegistryKey(Utils.rl("bore_head"));
    public static final ResourceKey<Registry<BoerHead>> OLD_BOER_HEAD =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("boers", "boer_head"));

    public static final ResourceKey<BoerHead> COPPER = ResourceKey.create(BOER_HEAD, Utils.rl("copper"));
    public static final ResourceKey<BoerHead> IRON = ResourceKey.create(BOER_HEAD, Utils.rl("iron"));
    public static final ResourceKey<BoerHead> DIAMOND = ResourceKey.create(BOER_HEAD, Utils.rl("diamond"));
    public static final ResourceKey<BoerHead> NETHERITE = ResourceKey.create(BOER_HEAD, Utils.rl("netherite"));
    public static final ResourceKey<BoerHead> GOLDEN = ResourceKey.create(BOER_HEAD, Utils.rl("golden"));
}
