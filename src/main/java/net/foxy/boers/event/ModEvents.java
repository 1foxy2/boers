package net.foxy.boers.event;

import net.foxy.boers.BoersMod;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.data.BoerHead;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod.EventBusSubscriber(modid = BoersMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void registerBoerHeadRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModRegistries.BOER_HEAD, BoerHead.CODEC, BoerHead.CODEC);
    }

}
