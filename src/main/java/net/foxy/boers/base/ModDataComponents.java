package net.foxy.boers.base;

import net.minecraft.core.Holder;
import net.foxy.boers.BoersMod;
import net.foxy.boers.data.BoerHead;
import net.foxy.boers.item.BoerContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BoersMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<BoerHead>>> BOER =
            COMPONENTS.registerComponentType("boer", builder -> builder.persistent(BoerHead.ITEM_CODEC)
                    .networkSynchronized(BoerHead.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_USED =
            COMPONENTS.registerComponentType("used", builder -> builder
                    .networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> USED_FOR =
            COMPONENTS.registerComponentType("used_for", builder -> builder
                    .networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DOUBLE =
            COMPONENTS.registerComponentType("double", builder -> builder
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BoerContents>> BOER_CONTENTS = COMPONENTS.registerComponentType(
            "boer_contents", builder -> builder.persistent(BoerContents.CODEC).networkSynchronized(BoerContents.STREAM_CODEC)
    );
}
