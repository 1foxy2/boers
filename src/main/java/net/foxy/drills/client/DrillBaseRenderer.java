package net.foxy.drills.client;

import net.foxy.drills.base.ModDataComponents;
import net.foxy.drills.base.ModModels;
import net.foxy.drills.event.ModClientEvents;
import net.foxy.drills.item.DrillContents;
import net.foxy.drills.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DrillBaseRenderer extends BlockEntityWithoutLevelRenderer {

    public DrillBaseRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        if (displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.FIXED) {
            DrillContents itemStack = stack.get(ModDataComponents.DRILL_CONTENTS);
            boolean flag = itemStack != null && !itemStack.isEmpty();
            if (!flag) {
                poseStack.translate(-1 * 0.0625, 3 * 0.0625, 0);
            }
            RenderUtils.renderItemModel(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, ModModels.DRILL_BASE_GUI);
            poseStack.translate(-1 * 0.0625, 1 * 0.0625, 0.002);

            if (flag) {
                RenderUtils.renderItemModel(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
            }
        } else {
            if (stack.getOrDefault(ModDataComponents.IS_USED, false)) {
                RandomSource randomSource = Minecraft.getInstance().level.getRandom();
                poseStack.translate(randomSource.nextFloat() / 100f, randomSource.nextFloat() / 100f, randomSource.nextFloat() / 100f);
            }

            if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                poseStack.scale(3.75f, 3.75f, 1.875f);
                poseStack.translate(-0.8f, 0.1, -0.15f);
                poseStack.mulPose(Axis.ZN.rotationDegrees(10));
            } else if (displayContext.firstPerson()) {
                poseStack.mulPose(Axis.YN.rotationDegrees(Mth.catmullrom(Mth.lerp(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), ModClientEvents.lastProgress, ModClientEvents.usingProgress) / 10f, 0, 0, 14, 100)));
            }

            RenderUtils.renderItemModel(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, ModModels.DRILL_BASE);
            poseStack.scale(0.5f, 0.5f, 1.01f);
            poseStack.translate(12 * 0.0625, 5 * 0.0625, -0.005f);
            DrillContents itemStack = stack.get(ModDataComponents.DRILL_CONTENTS);
            if (itemStack != null && !itemStack.isEmpty()) {
                RenderUtils.renderItemModel(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
            }
        }
        poseStack.popPose();
    }
}
