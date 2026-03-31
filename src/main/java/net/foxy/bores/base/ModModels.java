package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.foxy.bores.util.Utils;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = BoresMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModModels {
    public static final ModelResourceLocation BORE =
            new ModelResourceLocation(Utils.rl("bore_texture"), "inventory");
    public static final ModelResourceLocation BORE_GUI =
            new ModelResourceLocation(Utils.rl("bore_gui"), "inventory");

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(BORE);
        event.register(BORE_GUI);
    }
}

