package net.foxy.boers.network;

import net.foxy.boers.BoersMod;
import net.foxy.boers.network.c2s.SetUseBoerPacket;
import net.foxy.boers.network.c2s.TickBoerPacket;
import net.foxy.boers.util.Utils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(modid = BoersMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
                SetUseBoerPacket.class,
                SetUseBoerPacket::encode,
                SetUseBoerPacket::decode,
                SetUseBoerPacket::handle
                );
        INSTANCE.registerMessage(
                id,
                TickBoerPacket.class,
                TickBoerPacket::encode,
                TickBoerPacket::decode,
                TickBoerPacket::handle
                );
    }
}
