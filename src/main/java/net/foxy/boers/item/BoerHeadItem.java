package net.foxy.boers.item;

import com.mojang.logging.LogUtils;
import net.foxy.boers.BoersConfig;
import net.foxy.boers.data.BoerHead;
import net.foxy.boers.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

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

        return super.getDescriptionId(stack) + "." + head.getKey().location().toString().replace(":", ".");
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (BoersConfig.CONFIG.ENCHANTABLE_BOER_HEAD.get()) {
            return super.supportsEnchantment(Items.NETHERITE_PICKAXE.getDefaultInstance(), enchantment);
        }
        return super.supportsEnchantment(stack, enchantment);
    }
}
