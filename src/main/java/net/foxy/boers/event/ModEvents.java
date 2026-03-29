package net.foxy.boers.event;

import net.foxy.boers.BoresMod;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.data.BoreHead;
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
