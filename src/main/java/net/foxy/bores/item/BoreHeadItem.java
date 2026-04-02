package net.foxy.bores.item;

import net.foxy.bores.BoresConfig;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
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
    public Component getName(ItemStack itemStack) {
        Holder<BoreHead> head = Utils.getBoreHolder(itemStack);

        if (head == null) {
            return super.getName(itemStack);
        }

        return Component.translatable(getDescriptionId() + "." + head.getKey().identifier().toString().replace(":", "."));
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (BoresConfig.CONFIG.ENCHANTABLE_BORE_HEAD.get()) {
            return super.supportsEnchantment(Items.NETHERITE_PICKAXE.getDefaultInstance(), enchantment);
        }
        return super.supportsEnchantment(stack, enchantment);
    }
}
