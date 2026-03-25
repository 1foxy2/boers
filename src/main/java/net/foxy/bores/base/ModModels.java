package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.foxy.bores.util.Utils;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = BoresMod.MODID)
public final class ModModels {
    public static final ModelResourceLocation BOER_BASE =
            ModelResourceLocation.standalone(Utils.rl("item/bore_texture"));
    public static final ModelResourceLocation BOER_BASE_GUI =
            ModelResourceLocation.standalone(Utils.rl("item/bore_gui"));

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(BOER_BASE);
        event.register(BOER_BASE_GUI);
    }
}

