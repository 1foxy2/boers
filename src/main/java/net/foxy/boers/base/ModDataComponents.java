package net.foxy.boers.base;

import net.minecraft.core.Holder;
import net.foxy.boers.BoresMod;
import net.foxy.boers.data.BoreHead;
import net.foxy.boers.item.BoreContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BoresMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<BoreHead>>> BORE =
            COMPONENTS.registerComponentType("bore", builder -> builder.persistent(BoreHead.ITEM_CODEC)
                    .networkSynchronized(BoreHead.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_USED =
            COMPONENTS.registerComponentType("used", builder -> builder
                    .networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> USED_FOR =
            COMPONENTS.registerComponentType("used_for", builder -> builder
                    .networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DOUBLE =
            COMPONENTS.registerComponentType("double", builder -> builder
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BoreContents>> BORE_CONTENTS = COMPONENTS.registerComponentType(
            "bore_contents", builder -> builder.persistent(BoreContents.CODEC).networkSynchronized(BoreContents.STREAM_CODEC)
    );
}
