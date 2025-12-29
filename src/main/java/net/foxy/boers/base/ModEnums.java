package net.foxy.boers.base;

import com.mojang.logging.LogUtils;
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
            float originalDegrees = (float) -Math.toDegrees(model.getHead().xRot);
            if (originalDegrees > 0) {
                float degrees = originalDegrees / 90;
                model.rightArm.xRot = (float) -Math.toRadians(Mth.lerp(degrees, 0, 74.3736687906));
                model.rightArm.yRot = (float) -Math.toRadians(Mth.lerp(degrees, 0, 22.9193374794));
                model.rightArm.zRot = (float) Math.toRadians(Mth.lerp(degrees, 0, -35.6867205797));
                if (originalDegrees < 45) {
                    degrees = originalDegrees / 45;
                    model.leftArm.xRot = (float) -Math.toRadians(Mth.lerp(degrees, 29.1474262639, 90.4256902714));
                    model.leftArm.yRot = (float) -Math.toRadians(Mth.lerp(degrees, 7.4354722262, 30.7574339814));
                    model.leftArm.zRot = (float) Math.toRadians(Mth.lerp(degrees, -13.0643134295, -8.3843497195));
                } else if (originalDegrees < 67.5) {
                    degrees = (originalDegrees - 45) / 22.5f;
                    model.leftArm.xRot = (float) -Math.toRadians(Mth.lerp(degrees, 90.4256902714, 128.8673320113));
                    model.leftArm.yRot = (float) -Math.toRadians(Mth.lerp(degrees, 30.75743398140, 34.8837473246));
                    model.leftArm.zRot = (float) Math.toRadians(Mth.lerp(degrees, -8.3843497195, -10.1815232852));
                } else {
                    degrees = (originalDegrees - 67.5f) / 22.5f;
                    model.leftArm.xRot = (float) -Math.toRadians(Mth.lerp(degrees, 128.8673320113, 158.2697290842));
                    model.leftArm.yRot = (float) -Math.toRadians(Mth.lerp(degrees, 34.8837473246, 28.1434154178));
                    model.leftArm.zRot = (float) Math.toRadians(Mth.lerp(degrees, -10.1815232852, -6.8963601602));
                }
                if (false) {
                    model.rightArm.xRot = (float) -Math.toRadians(74.3736687906);
                    model.rightArm.yRot = (float) -Math.toRadians(22.9193374794);
                    model.rightArm.zRot = (float) Math.toRadians(-35.6867205797);
                    model.leftArm.xRot = (float) -Math.toRadians(167.3593651783);
                    model.leftArm.yRot = (float) -Math.toRadians(-3.0293377767);
                    model.leftArm.zRot = (float) Math.toRadians(-5.0689762826);
                }
            }
        } else {
            model.leftArm.xRot = 0;
            model.rightArm.xRot = (float) -Math.toRadians(35);
            model.leftArm.yRot = 0;
            model.leftArm.zRot = 0;
        }
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
