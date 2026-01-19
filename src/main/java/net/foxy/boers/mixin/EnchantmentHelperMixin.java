package net.foxy.boers.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import net.foxy.boers.base.ModItems;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @WrapOperation(
            method = "getRandomItemWith",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(" +
                            "Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private static Object useNeoMethod(ItemStack instance, DataComponentType dataComponentType,
                                       Object o, Operation<Object> original,
                                       @Local(argsOnly = true) LivingEntity entity) {
        if (instance.is(ModItems.BOER_BASE)) {
            return instance.getAllEnchantments(entity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT));
        }

        return original.call(instance, dataComponentType, o);
    }
}
