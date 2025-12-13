package net.foxy.boers.network.c2s;

import net.foxy.boers.item.BoerBaseItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TickBoerPacket(int progress) {
    public static void encode(TickBoerPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.progress);
    }

    public static TickBoerPacket decode(FriendlyByteBuf buf) {
        return new TickBoerPacket(buf.readInt());
    }

    public static void handle(TickBoerPacket payLoad, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ItemStack mainHandItem = ctx.get().getSender().getMainHandItem();
            if (mainHandItem.getItem() instanceof BoerBaseItem boerBaseItem) {
                boerBaseItem.onAttackTick(ctx.get().getSender().level(), ctx.get().getSender(), mainHandItem, payLoad.progress());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
