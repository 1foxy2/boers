package net.foxy.boers.network.c2s;

import net.foxy.boers.item.BoreItem;
import net.foxy.boers.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetUseBorePacket(boolean used) {
    public static void encode(SetUseBorePacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.used);
    }

    public static SetUseBorePacket decode(FriendlyByteBuf buf) {
        return new SetUseBorePacket(buf.readBoolean());
    }

    public static void handle(SetUseBorePacket payLoad, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ItemStack mainHandItem = ctx.get().getSender().getMainHandItem();
            if (mainHandItem.getItem() instanceof BoreItem) {
                Utils.setUsed(mainHandItem, payLoad.used);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
