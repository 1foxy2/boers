package net.foxy.boers.network.c2s;

import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.item.BoerBaseItem;
import net.foxy.boers.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetUseBoerPacket(boolean used) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, SetUseBoerPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SetUseBoerPacket::used,
            SetUseBoerPacket::new
    );

    public static final Type<SetUseBoerPacket> TYPE = new Type<>(Utils.rl("use_bore"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(SetUseBoerPacket payLoad, IPayloadContext ctx) {
        ItemStack mainHandItem = ctx.player().getMainHandItem();
        if (mainHandItem.getItem() instanceof BoerBaseItem) {
            Utils.setUsed(mainHandItem, payLoad.used);
        }
    }
}
