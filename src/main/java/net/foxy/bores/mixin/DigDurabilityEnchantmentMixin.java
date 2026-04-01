package net.foxy.bores.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.foxy.bores.BoresConfig;
import net.foxy.bores.base.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DigDurabilityEnchantment.class)
public class DigDurabilityEnchantmentMixin {
    @ModifyReturnValue(
            method = "canEnchant",
            at = @At("RETURN")
    )
    private boolean makeEnchantable(boolean original, @Local(argsOnly = true) ItemStack item) {
        if (item.is(ModItems.BORE_HEAD.get()) && !BoresConfig.CONFIG.ENCHANTABLE_BORE_HEAD.get()) {
            return false;
        }
        return original;
    }
}
