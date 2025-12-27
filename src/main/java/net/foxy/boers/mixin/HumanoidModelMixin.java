package net.foxy.boers.mixin;

import net.foxy.boers.base.ModEnums;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.model.HumanoidModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    @Shadow
    public HumanoidModel.ArmPose rightArmPose;

    @Shadow
    public HumanoidModel.ArmPose leftArmPose;

    @ModifyExpressionValue(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/model/HumanoidModel;leftArmPose:Lnet/minecraft/client/model/HumanoidModel$ArmPose;", ordinal = 1))
    public HumanoidModel.ArmPose removeBob(HumanoidModel.ArmPose original) {

        return original == ModEnums.BOER_SINGLE_STANDING_POS.getValue() || original == ModEnums.BOER_STANDING_POS.getValue() || rightArmPose == ModEnums.BOER_STANDING_POS.getValue() ? HumanoidModel.ArmPose.SPYGLASS : original;
    }

    @ModifyExpressionValue(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/model/HumanoidModel;rightArmPose:Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
                    ordinal = 1
            )
    )
    public HumanoidModel.ArmPose removeBobRight(HumanoidModel.ArmPose original) {
        return original == ModEnums.BOER_SINGLE_STANDING_POS.getValue() || original == ModEnums.BOER_STANDING_POS.getValue() || leftArmPose == ModEnums.BOER_STANDING_POS.getValue() ? HumanoidModel.ArmPose.SPYGLASS : original;
    }
}
