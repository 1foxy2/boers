package net.foxy.drills.network.c2s;

import net.foxy.drills.base.ModDataComponents;
import net.foxy.drills.item.DrillBaseItem;
import net.foxy.drills.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetUseDrillPacket(boolean used) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, SetUseDrillPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SetUseDrillPacket::used,
            SetUseDrillPacket::new
    );

    public static final Type<SetUseDrillPacket> TYPE = new Type<>(Utils.rl("use_drill"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(SetUseDrillPacket payLoad, IPayloadContext ctx) {
        ItemStack mainHandItem = ctx.player().getMainHandItem();
        if (mainHandItem.getItem() instanceof DrillBaseItem) {
            mainHandItem.set(ModDataComponents.IS_USED, payLoad.used);
        }
    }
}
