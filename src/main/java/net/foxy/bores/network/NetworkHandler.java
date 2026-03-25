package net.foxy.bores.network;

import net.foxy.bores.BoresMod;
import net.foxy.bores.network.c2s.SetUseBorePacket;
import net.foxy.bores.network.c2s.TickBorePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = BoresMod.MODID)
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "3";

    @SubscribeEvent
    public static void onRegisterPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToServer(
                TickBorePacket.TYPE,
                        TickBorePacket.STREAM_CODEC,
                        TickBorePacket::handle)
                .playToServer(
                        SetUseBorePacket.TYPE,
                        SetUseBorePacket.STREAM_CODEC,
                        SetUseBorePacket::handle);
    }
}
