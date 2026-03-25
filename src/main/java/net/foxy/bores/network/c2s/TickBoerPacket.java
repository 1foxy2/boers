package net.foxy.bores.network.c2s;

import net.foxy.bores.item.BoerBaseItem;
import net.foxy.bores.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TickBoerPacket(int progress) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, TickBoerPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            TickBoerPacket::progress,
            TickBoerPacket::new
    );

    public static final Type<TickBoerPacket> TYPE = new Type<>(Utils.rl("tick_bore"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(TickBoerPacket payLoad, IPayloadContext ctx) {
        ItemStack mainHandItem = ctx.player().getMainHandItem();
        if (mainHandItem.getItem() instanceof BoerBaseItem boerBaseItem) {
            boerBaseItem.onAttackTick(ctx.player().level(), ctx.player(), mainHandItem, payLoad.progress());
        }
    }
}
