package net.foxy.boers.base;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class ModEnums {
    public static final EnumProxy<HumanoidModel.ArmPose> BOER_STANDING_POS = new EnumProxy<>(
            HumanoidModel.ArmPose.class, true, (IArmPoseTransformer)
            ModEnums::applyPose
    );
    public static final EnumProxy<HumanoidModel.ArmPose> BOER_SINGLE_STANDING_POS = new EnumProxy<>(
            HumanoidModel.ArmPose.class, false, (IArmPoseTransformer)
            ModEnums::applySinglePose
    );

    public static void applyPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        if (arm == HumanoidArm.RIGHT) {
            model.leftArm.xRot = (float) -Math.toRadians(35);
            model.rightArm.xRot = 0;
        } else {
            model.leftArm.xRot = 0;
            model.rightArm.xRot = (float) -Math.toRadians(35);
        }
        model.rightArm.yRot = 0;
        model.leftArm.yRot = 0;
        model.rightArm.zRot = 0;
        model.leftArm.zRot = 0;
    }

    public static void applySinglePose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        if (arm == HumanoidArm.RIGHT) {
            model.rightArm.xRot = (float) (model.head.xRot - Math.toRadians(90));
            model.rightArm.yRot = model.head.yRot;
            model.rightArm.zRot = 0;
        } else {
            model.leftArm.xRot = (float) (model.head.xRot - Math.toRadians(90));
            model.leftArm.yRot = model.head.yRot;
            model.leftArm.zRot = 0;
        }
    }
}
