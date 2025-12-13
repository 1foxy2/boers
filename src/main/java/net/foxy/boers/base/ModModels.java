package net.foxy.boers.base;

import net.foxy.boers.BoersMod;
import net.foxy.boers.util.Utils;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = BoersMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModModels {
    public static final ModelResourceLocation BOER_BASE =
            new ModelResourceLocation(Utils.rl("boer_base_texture"), "inventory");
    public static final ModelResourceLocation BOER_BASE_GUI =
            new ModelResourceLocation(Utils.rl("boer_base_gui"), "inventory");

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(BOER_BASE);
        event.register(BOER_BASE_GUI);
    }
}

