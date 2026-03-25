package net.foxy.bores.event;

import net.foxy.bores.BoresMod;
import net.foxy.bores.base.ModRegistries;
import net.foxy.bores.data.BoerHead;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = BoresMod.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void registerBoerHeadRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModRegistries.BOER_HEAD, BoerHead.CODEC, BoerHead.CODEC);
    }

}
