package net.foxy.bores.mixin;

import net.foxy.bores.util.Utils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyArg(
            method = "breakItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;spawnItemParticles(Lnet/minecraft/world/item/ItemStack;I)V"
            ),
            index = 0
    )
    private static ItemStack particles(ItemStack stack) {
        ItemStack boer = Utils.getBoerContentsOrEmpty(stack).getItemUnsafe();
        if (!boer.isEmpty()) {
            return boer;
        }
        return stack;
    }
}
