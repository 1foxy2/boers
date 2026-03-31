package net.foxy.boers.item;

import net.foxy.boers.BoresConfig;
import net.foxy.boers.data.BoreHead;
import net.foxy.boers.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

public class BoreHeadItem extends Item {
    public BoreHeadItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        BoreHead head = Utils.getBore(stack);
        return head != null ? head.durability() : 0;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        Holder<BoreHead> head = Utils.getBoreHolder(stack);

        if (head == null) {
            return super.getDescriptionId(stack);
        }

        return super.getDescriptionId(stack) + "." + head.unwrapKey().get().location().toString().replace(":", ".");
    }


    /*@Override TODO
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (BoresConfig.CONFIG.ENCHANTABLE_BORE_HEAD.get()) {
            return super.supportsEnchantment(Items.NETHERITE_PICKAXE.getDefaultInstance(), enchantment);
        }
        return super.supportsEnchantment(stack, enchantment);
    }*/
}
