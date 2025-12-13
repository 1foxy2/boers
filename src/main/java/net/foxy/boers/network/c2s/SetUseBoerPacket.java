package net.foxy.boers.network.c2s;

import net.foxy.boers.item.BoerBaseItem;
import net.foxy.boers.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetUseBoerPacket(boolean used) {
    public static void encode(SetUseBoerPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.used);
    }

    public static SetUseBoerPacket decode(FriendlyByteBuf buf) {
        return new SetUseBoerPacket(buf.readBoolean());
    }

    public static void handle(SetUseBoerPacket payLoad, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ItemStack mainHandItem = ctx.get().getSender().getMainHandItem();
            if (mainHandItem.getItem() instanceof BoerBaseItem) {
                Utils.setUsed(mainHandItem, payLoad.used);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
