package net.foxy.bores.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.foxy.bores.base.ModEnums;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    @WrapWithCondition(
            method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/AnimationUtils;" +
                            "bobModelPart(Lnet/minecraft/client/model/geom/ModelPart;FF)V",
                    ordinal = 0
            )
    )
    public boolean removeBobRight(ModelPart modelPart, float ageInTicks, float scale,
                             @Local(ordinal = 0) HumanoidModel.ArmPose leftArm,
                             @Local(ordinal = 1) HumanoidModel.ArmPose rightArm
    ) {
        return rightArm != ModEnums.BORE_SINGLE_STANDING_POS.getValue() &&
                rightArm != ModEnums.BORE_STANDING_POS.getValue() && leftArm != ModEnums.BORE_STANDING_POS.getValue();
    }

    @WrapWithCondition(
            method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/AnimationUtils;" +
                            "bobModelPart(Lnet/minecraft/client/model/geom/ModelPart;FF)V",
                    ordinal = 1
            )
    )
    public boolean removeBobLeft(ModelPart modelPart, float ageInTicks, float scale,
                             @Local(ordinal = 0) HumanoidModel.ArmPose leftArm,
                             @Local(ordinal = 1) HumanoidModel.ArmPose rightArm
    ) {
        return leftArm != ModEnums.BORE_SINGLE_STANDING_POS.getValue() &&
                leftArm != ModEnums.BORE_STANDING_POS.getValue() && rightArm != ModEnums.BORE_STANDING_POS.getValue();
    }
}
