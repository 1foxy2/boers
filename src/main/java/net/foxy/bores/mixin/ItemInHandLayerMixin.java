package net.foxy.bores.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.foxy.bores.base.ModEnums;
import net.foxy.bores.base.ModItems;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ArmedEntityRenderState.class)
public class ItemInHandLayerMixin {
    @ModifyArg(
            method = "extractArmedEntityRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemModelResolver;updateForLiving(" +
                            "Lnet/minecraft/client/renderer/item/ItemStackRenderState;" +
                            "Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;" +
                            "Lnet/minecraft/world/entity/LivingEntity;)V",
                    ordinal = 0
            ),
            index = 2
    )
    private static ItemDisplayContext useSingleDisplay(ItemDisplayContext displayContext,
                                                       @Local(argsOnly = true) LivingEntity entity) {
        return entity.getItemHeldByArm(HumanoidArm.RIGHT).is(ModItems.BORE) && !entity.getItemHeldByArm(HumanoidArm.LEFT).isEmpty() ?
                ModEnums.THIRDPERSON_RIGHTHAND_SINGLE.getValue() : displayContext;
    }
    @ModifyArg(
            method = "extractArmedEntityRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemModelResolver;updateForLiving(" +
                            "Lnet/minecraft/client/renderer/item/ItemStackRenderState;" +
                            "Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;" +
                            "Lnet/minecraft/world/entity/LivingEntity;)V",
                    ordinal = 1
            ),
            index = 2
    )
    private static ItemDisplayContext useSingleDisplayOffHand(ItemDisplayContext displayContext,
                                                              @Local(argsOnly = true) LivingEntity entity) {
        return entity.getItemHeldByArm(HumanoidArm.LEFT).is(ModItems.BORE) && !entity.getItemHeldByArm(HumanoidArm.RIGHT).isEmpty() ?
                ModEnums.THIRDPERSON_LEFTHAND_SINGLE.getValue() : displayContext;
    }
}
