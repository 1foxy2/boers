package net.foxy.boers.base;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
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
          //  model.getHead().xRot = (float) -Math.toRadians(90);
            float degrees = (float) Math.toDegrees(model.getHead().xRot);
            if (degrees < 0) {
                degrees = degrees / -90;
                model.rightArm.xRot = (float) -Math.toRadians(Mth.lerp(degrees, 0, 67.9577479627));//0 + (model.getHead().xRot / 1.5f);
                model.rightArm.yRot = (float) -Math.toRadians(Mth.lerp(degrees, 0, 30.5018140245));//0 + (model.getHead().xRot / 3);
                model.rightArm.zRot = (float) Math.toRadians(Mth.lerp(degrees, 0, -38.5793078521));//0 + (model.getHead().xRot / 3);
            }
        } else {
            model.leftArm.xRot = 0;
            model.rightArm.xRot = (float) -Math.toRadians(35);
        }
        model.leftArm.yRot = 0;
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
