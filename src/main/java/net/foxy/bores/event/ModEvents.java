package net.foxy.bores.event;

import net.foxy.bores.BoresMod;
import net.foxy.bores.base.ModRegistries;
import net.foxy.bores.data.BoreHead;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod.EventBusSubscriber(modid = BoresMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void registerBoreHeadRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModRegistries.BORE_HEAD, BoreHead.CODEC, BoreHead.NETWORK_CODEC);
    }

}
