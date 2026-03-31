package net.foxy.boers.network;

import net.foxy.boers.BoresMod;
import net.foxy.boers.network.c2s.SetUseBorePacket;
import net.foxy.boers.network.c2s.TickBorePacket;
import net.foxy.boers.util.Utils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(modid = BoresMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            Utils.rl("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    @SubscribeEvent
    public static void onRegisterPayloads(final FMLCommonSetupEvent event) {
        int id = 0;
        INSTANCE.registerMessage(
                id++,
                SetUseBorePacket.class,
                SetUseBorePacket::encode,
                SetUseBorePacket::decode,
                SetUseBorePacket::handle
                );
        INSTANCE.registerMessage(
                id,
                TickBorePacket.class,
                TickBorePacket::encode,
                TickBorePacket::decode,
                TickBorePacket::handle
                );
    }
}
