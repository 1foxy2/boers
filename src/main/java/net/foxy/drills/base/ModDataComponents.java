package net.foxy.drills.base;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Lazy;
import net.foxy.drills.DrillsMod;
import net.foxy.drills.data.DrillHead;
import net.foxy.drills.item.DrillContents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, DrillsMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<DrillHead>>> DRILL =
            COMPONENTS.registerComponentType("drill", builder -> builder.persistent(DrillHead.ITEM_CODEC)
                    .networkSynchronized(DrillHead.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_USED =
            COMPONENTS.registerComponentType("o_used", builder -> builder
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DrillContents>> DRILL_CONTENTS = COMPONENTS.registerComponentType(
            "drill_contents", builder -> builder.persistent(DrillContents.CODEC).networkSynchronized(DrillContents.STREAM_CODEC)
    );
}
