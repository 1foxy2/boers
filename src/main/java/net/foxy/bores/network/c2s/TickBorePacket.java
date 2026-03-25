package net.foxy.bores.network.c2s;

import net.foxy.bores.item.BoreItem;
import net.foxy.bores.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TickBorePacket(int progress) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, TickBorePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            TickBorePacket::progress,
            TickBorePacket::new
    );

    public static final Type<TickBorePacket> TYPE = new Type<>(Utils.rl("tick_bore"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(TickBorePacket payLoad, IPayloadContext ctx) {
        ItemStack mainHandItem = ctx.player().getMainHandItem();
        if (mainHandItem.getItem() instanceof BoreItem boreItem) {
            boreItem.onAttackTick(ctx.player().level(), ctx.player(), mainHandItem, payLoad.progress());
        }
    }
}
