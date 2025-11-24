package net.foxy.drills.network.c2s;

import net.foxy.drills.item.DrillBaseItem;
import net.foxy.drills.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TickDrillPacket(int progress) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, TickDrillPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            TickDrillPacket::progress,
            TickDrillPacket::new
    );

    public static final Type<TickDrillPacket> TYPE = new Type<>(Utils.rl("tick_drill"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(TickDrillPacket payLoad, IPayloadContext ctx) {
        ItemStack mainHandItem = ctx.player().getMainHandItem();
        if (mainHandItem.getItem() instanceof DrillBaseItem drillBaseItem) {
            drillBaseItem.onAttackTick(ctx.player().level(), ctx.player(), mainHandItem, payLoad.progress());
        }
    }
}
