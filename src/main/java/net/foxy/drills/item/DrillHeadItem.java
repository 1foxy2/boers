package net.foxy.drills.item;

import net.foxy.drills.util.Utils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DrillHeadItem extends Item {
    public DrillHeadItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Utils.drillOrDefault(stack).durability();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return super.getDescriptionId(stack) + Utils.drillOrDefault(stack).id().toString().replace(":", ".");
    }
}
