package net.foxy.boers.event;

import net.foxy.boers.BoresMod;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.data.BoreHead;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod.EventBusSubscriber(modid = BoresMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void registerBoerHeadRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModRegistries.BORE_HEAD, BoreHead.CODEC, BoreHead.NETWORK_CODEC);
    }

}
