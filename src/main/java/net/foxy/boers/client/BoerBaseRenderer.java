package net.foxy.boers.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.foxy.boers.base.ModModels;
import net.foxy.boers.data.BoerHead;
import net.foxy.boers.event.ModClientEvents;
import net.foxy.boers.util.RenderUtils;
import net.foxy.boers.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BoerBaseRenderer extends BlockEntityWithoutLevelRenderer {

    public BoerBaseRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        if (displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.FIXED || displayContext == ItemDisplayContext.GROUND) {
            ItemStack itemStack = Utils.getBoerContents(stack);
            boolean flag = itemStack != null && !itemStack.isEmpty();
            if (!flag) {
                poseStack.translate(-1 * 0.0625, 3 * 0.0625, 0);
            }
            RenderUtils.renderItemModel(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, ModModels.BOER_BASE_GUI);
            poseStack.translate(-1 * 0.0625, 1 * 0.0625, 0.002);

            if (flag) {
                RenderUtils.renderItemModel(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
            }
        } else {
            if (Utils.isUsed(stack)) {
                RandomSource randomSource = Minecraft.getInstance().level.getRandom();
                float usedFor = 100f - Math.min(Utils.getUsedFor(stack), 100) / 100f;
                poseStack.translate(randomSource.nextFloat() / usedFor, randomSource.nextFloat() / usedFor, randomSource.nextFloat() / usedFor);
            }

            if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                poseStack.scale(3.75f, 3.75f, 1.875f);
                poseStack.translate(-0.8f, 0.1, -0.15f);
                poseStack.mulPose(Axis.ZN.rotationDegrees(10));
            } else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                poseStack.scale(3.75f, 3.75f, 1.875f);
                poseStack.translate(-0.8f, -0.227, -0.15f);
                poseStack.mulPose(Axis.ZP.rotationDegrees(10));
                poseStack.rotateAround(Axis.YP.rotationDegrees(180), 0.95f, 0, 0.5f);
            } else if (displayContext.firstPerson()) {
                float angle = Mth.catmullrom(Mth.lerp(Minecraft.getInstance().getPartialTick(), ModClientEvents.lastProgress, ModClientEvents.usingProgress) / 10f, 0, 0, 14, 100);
                if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                    poseStack.mulPose(Axis.YN.rotationDegrees(angle));
                } else {
                    poseStack.rotateAround(Axis.YP.rotationDegrees(angle), 0, 0, 1);
                }
            }

            RenderUtils.renderItemModel(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, ModModels.BOER_BASE);
            poseStack.scale(0.5f, 0.5f, 1.01f);
            poseStack.translate(12 * 0.0625, 5 * 0.0625, -0.005f);
            ItemStack itemStack = Utils.getBoerContents(stack);
            if (itemStack != null && !itemStack.isEmpty()) {
                BoerHead boerHead = Utils.getBoer(itemStack);
                int usedFor = 0;
                if (boerHead != null) {
                    usedFor = boerHead.getMaxAcceleration(stack);
                }
                int color = Math.max(255 - usedFor, 255 - BoersClientConfig.CONFIG.MAX_BOER_HEATING.get());
                RenderUtils.renderItemModel(stack, displayContext, poseStack, buffer, packedLight, packedOverlay, 255, color, color, 255);
            }
        }
        poseStack.popPose();
    }
}
