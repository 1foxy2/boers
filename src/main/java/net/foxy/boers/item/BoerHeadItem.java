package net.foxy.boers.item;

import net.foxy.boers.data.BoerHead;
import net.foxy.boers.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BoerHeadItem extends Item {
    public BoerHeadItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        BoerHead head = Utils.getBoer(stack);
        return head != null ? head.durability() : 0;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        Holder<BoerHead> head = Utils.getBoerHolder(stack);

        if (head == null) {
            return super.getDescriptionId(stack);
        }

        return super.getDescriptionId(stack) + "." + head.unwrapKey().get().location().toString().replace(":", ".");
    }
}
