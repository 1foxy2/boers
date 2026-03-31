package net.foxy.bores.network.c2s;

import net.foxy.bores.item.BoreItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TickBorePacket(int progress) {
    public static void encode(TickBorePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.progress);
    }

    public static TickBorePacket decode(FriendlyByteBuf buf) {
        return new TickBorePacket(buf.readInt());
    }

    public static void handle(TickBorePacket payLoad, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ItemStack mainHandItem = ctx.get().getSender().getMainHandItem();
            if (mainHandItem.getItem() instanceof BoreItem bore) {
                bore.onAttackTick(ctx.get().getSender().level(), ctx.get().getSender(), mainHandItem, payLoad.progress());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
