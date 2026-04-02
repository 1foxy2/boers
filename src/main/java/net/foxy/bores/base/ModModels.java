package net.foxy.bores.base;

import com.google.common.collect.ImmutableMap;
import net.foxy.bores.BoresMod;
import net.foxy.bores.data.BoreColoring;
import net.foxy.bores.util.Utils;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT, modid = BoresMod.MODID)
public final class ModModels {
    public static final Map<DyeColor, StandaloneModelKey<QuadCollection>> BORES = Util.make(
            new ImmutableMap.Builder<DyeColor, StandaloneModelKey<QuadCollection>>(),
            builder -> {
                for (DyeColor dyeColor : BoreColoring.ALLOWED_COLORS) {
                    builder.put(dyeColor, new StandaloneModelKey<>(() -> "bores: Bore Model " + dyeColor.getSerializedName()));
                }
    }).build();
            ;

    public static final Map<DyeColor, StandaloneModelKey<QuadCollection>> BORES_GUI = Util.make(
            new ImmutableMap.Builder<DyeColor, StandaloneModelKey<QuadCollection>>(),
            builder -> {
                for (DyeColor dyeColor : BoreColoring.ALLOWED_COLORS) {
                    builder.put(dyeColor,  new StandaloneModelKey<>(() -> "bores: Bore Gui Model " + dyeColor.getSerializedName()));
                }
            }).build();

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterStandalone event) {
        BORES.forEach((dyeColor, modelKey) -> {
            event.register(modelKey, SimpleUnbakedStandaloneModel.quadCollection(
                    Utils.rl("item/bore_" + dyeColor.getSerializedName())
            ));
        });
        BORES_GUI.forEach(((dyeColor, modelKey) -> {
            event.register(modelKey, SimpleUnbakedStandaloneModel.quadCollection(
                    Utils.rl("item/bore_gui_" + dyeColor.getSerializedName())
            ));
        }));
    }
}

