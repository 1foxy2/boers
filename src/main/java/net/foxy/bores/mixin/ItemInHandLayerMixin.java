package net.foxy.bores.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.foxy.bores.base.ModEnums;
import net.foxy.bores.base.ModItems;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {
    @ModifyArg(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/ItemInHandLayer;renderArmWithItem(" +
                            "Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;" +
                            "Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;" +
                            "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                    ordinal = 0
            ),
            index = 2
    )
    private ItemDisplayContext useSingleDisplay(ItemDisplayContext displayContext,
                                                      @Local(ordinal = 0) ItemStack offStack,
                                                      @Local(ordinal = 1) ItemStack mainStack) {
        return mainStack.is(ModItems.BORE) && !offStack.isEmpty() ?
                ModEnums.THIRDPERSON_RIGHTHAND_SINGLE.getValue() : displayContext;
    }
    @ModifyArg(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/ItemInHandLayer;renderArmWithItem(" +
                            "Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;" +
                            "Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;" +
                            "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                    ordinal = 1
            ),
            index = 2
    )
    private ItemDisplayContext useSingleDisplayOffHand(ItemDisplayContext displayContext,
                                                      @Local(ordinal = 0) ItemStack offStack,
                                                      @Local(ordinal = 1) ItemStack mainStack) {
        return offStack.is(ModItems.BORE) && !mainStack.isEmpty() ?
                ModEnums.THIRDPERSON_LEFTHAND_SINGLE.getValue() : displayContext;
    }
}
