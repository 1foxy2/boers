package net.foxy.drills.network;

import net.foxy.drills.DrillsMod;
import net.foxy.drills.network.c2s.SetUseDrillPacket;
import net.foxy.drills.network.c2s.TickDrillPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = DrillsMod.MODID)
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "3";

    @SubscribeEvent
    public static void onRegisterPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToServer(
                TickDrillPacket.TYPE,
                        TickDrillPacket.STREAM_CODEC,
                        TickDrillPacket::handle)
                .playToServer(
                        SetUseDrillPacket.TYPE,
                        SetUseDrillPacket.STREAM_CODEC,
                        SetUseDrillPacket::handle);
    }
}
