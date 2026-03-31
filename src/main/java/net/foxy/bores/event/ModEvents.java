package net.foxy.bores.event;

import net.foxy.bores.BoresMod;
import net.foxy.bores.base.ModRegistries;
import net.foxy.bores.data.BoreHead;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = BoresMod.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void registerBoreHeadRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModRegistries.BORE_HEAD, BoreHead.CODEC, BoreHead.CODEC);
    }

}
