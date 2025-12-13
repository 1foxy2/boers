package net.foxy.boers.base;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class ModEnums {
    public static final HumanoidModel.ArmPose BOER_STANDING_POS = HumanoidModel.ArmPose.create(
            "BOERS_BOER_HOLDING",true, ModEnums::applyPose
    );

    public static void init() {

    }

    public static void applyPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        model.rightArm.xRot = 0;
        model.leftArm.xRot = (float) - Math.toRadians(35);
        model.rightArm.yRot = 0;
        model.leftArm.yRot = 0;
        model.rightArm.zRot = 0;
        model.leftArm.zRot = 0;
    }
}
