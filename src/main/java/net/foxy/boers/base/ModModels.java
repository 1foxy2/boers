package net.foxy.boers.base;

import net.foxy.boers.BoresMod;
import net.foxy.boers.util.Utils;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = BoresMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModModels {
    public static final ModelResourceLocation BORE_BASE =
            new ModelResourceLocation(Utils.rl("bore_texture"), "inventory");
    public static final ModelResourceLocation BORE_BASE_GUI =
            new ModelResourceLocation(Utils.rl("bore_gui"), "inventory");

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(BORE_BASE);
        event.register(BORE_BASE_GUI);
    }
}

