package net.foxy.bores.network.c2s;

import net.foxy.bores.item.BoreItem;
import net.foxy.bores.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetUseBorePacket(boolean used) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, SetUseBorePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SetUseBorePacket::used,
            SetUseBorePacket::new
    );

    public static final Type<SetUseBorePacket> TYPE = new Type<>(Utils.rl("use_bore"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(SetUseBorePacket payLoad, IPayloadContext ctx) {
        ItemStack mainHandItem = ctx.player().getMainHandItem();
        if (mainHandItem.getItem() instanceof BoreItem) {
            Utils.setUsed(mainHandItem, payLoad.used);
        }
    }
}
