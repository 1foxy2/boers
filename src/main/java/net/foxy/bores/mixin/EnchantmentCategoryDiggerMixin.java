package net.foxy.bores.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.foxy.bores.BoresConfig;
import net.foxy.bores.base.ModItems;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.item.enchantment.EnchantmentCategory$7")
public class EnchantmentCategoryDiggerMixin {
    @ModifyReturnValue(
            method = "canEnchant",
            at = @At("RETURN")
    )
    private boolean makeEnchantable(boolean original, @Local(argsOnly = true) Item item) {
        return original || item == ModItems.BORE_HEAD.get() && BoresConfig.CONFIG.ENCHANTABLE_BORE_HEAD.get();
    }
}
