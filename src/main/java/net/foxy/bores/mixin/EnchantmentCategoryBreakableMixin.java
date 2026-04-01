package net.foxy.bores.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import net.foxy.bores.BoresConfig;
import net.foxy.bores.base.ModItems;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.item.enchantment.EnchantmentCategory$10")
public class EnchantmentCategoryBreakableMixin {
    @ModifyReturnValue(
            method = "canEnchant",
            at = @At("RETURN")
    )
    private boolean makeEnchantable(boolean original, @Local(argsOnly = true) Item item) {
        if (item == ModItems.BORE_HEAD.get() && !BoresConfig.CONFIG.ENCHANTABLE_BORE_HEAD.get()) {
            return false;
        }
        return original;
    }
}
