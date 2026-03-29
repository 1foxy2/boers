package net.foxy.boers.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.foxy.boers.base.ModModels;
import net.foxy.boers.data.BoreHead;
import net.foxy.boers.event.ModClientEvents;
import net.foxy.boers.item.BoreContents;
import net.foxy.boers.util.RenderUtils;
import net.foxy.boers.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;

public class BoreRenderer extends BlockEntityWithoutLevelRenderer {

    public BoreRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        if (displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.FIXED || displayContext == ItemDisplayContext.GROUND) {
            BoreContents itemStack = Utils.getBoreContents(stack);
            boolean flag = itemStack != null && !itemStack.isEmpty();
            if (!flag) {
                poseStack.translate(-1 * 0.0625, 3 * 0.0625, 0);
            }
            RenderUtils.renderItemModel(stack, RenderType.CUTOUT, displayContext, poseStack, buffer, packedLight, packedOverlay, ModModels.BORE_BASE_GUI);
            poseStack.translate(-1 * 0.0625, 1 * 0.0625, 0.002);

            if (flag) {
                RenderUtils.renderItemModel(stack, RenderType.CUTOUT, displayContext, poseStack, buffer, packedLight, packedOverlay);
            }
        } else {
            if (Utils.isUsed(stack)) {
                RandomSource randomSource = Minecraft.getInstance().level.getRandom();
                float usedFor = 100f - Math.min(Utils.getUsedFor(stack), 100) / 100f;
                poseStack.translate(randomSource.nextFloat() / usedFor, randomSource.nextFloat() / usedFor, randomSource.nextFloat() / usedFor);
            }

            if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                if (Utils.getDouble(stack)) {
                    poseStack.scale(3.75f, 3.75f, 1.875f);
                    poseStack.translate(-0.8f, 0.1, -0.15f);
                    poseStack.mulPose(Axis.ZN.rotationDegrees(10));
                } else {
                    poseStack.translate(1.42, -2.1, -0.3);
                    poseStack.mulPose(Axis.YN.rotationDegrees(90));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(45));
                    poseStack.scale(3.75f, 3.75f, 1.875f);
                }
            } else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                if (Utils.getDouble(stack)) {
                    poseStack.scale(3.75f, 3.75f, 1.875f);
                    poseStack.translate(-0.8f, -0.227, -0.15f);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(10));
                    poseStack.rotateAround(Axis.YP.rotationDegrees(180), 0.95f, 0, 0.5f);
                } else {
                    poseStack.translate(1.42, -2.1, -0.3);
                    poseStack.mulPose(Axis.YN.rotationDegrees(90));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(45));
                    poseStack.scale(3.75f, 3.75f, 1.875f);
                }
            } else if (displayContext.firstPerson()) {
                float angle = Mth.catmullrom(Mth.lerp(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), ModClientEvents.lastProgress, ModClientEvents.usingProgress) / 10f, 0, 0, 14, 100);
                if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                    poseStack.mulPose(Axis.YN.rotationDegrees(angle));
                } else {
                    poseStack.rotateAround(Axis.YP.rotationDegrees(angle), 0, 0, 1);
                }
            }

            RenderUtils.renderItemModel(stack, NeoForgeRenderTypes.ITEM_LAYERED_CUTOUT.get(), displayContext, poseStack, buffer, packedLight, packedOverlay, ModModels.BORE_BASE);
            poseStack.scale(0.5f, 0.5f, 1.01f);
            poseStack.translate(12 * 0.0625, 5 * 0.0625, -0.005f);
            BoreContents itemStack = Utils.getBoreContents(stack);
            if (itemStack != null && !itemStack.isEmpty()) {
                BoreHead boreHead = Utils.getBore(itemStack.getItemUnsafe());
                int usedFor = 0;
                if (boreHead != null) {
                    usedFor = boreHead.getMaxAcceleration(stack);
                }
                int color = Math.max(255 - usedFor, 255 - BoresClientConfig.CONFIG.MAX_BORE_HEATING.get());
                RenderUtils.renderItemModel(stack, NeoForgeRenderTypes.ITEM_LAYERED_CUTOUT.get(), displayContext, poseStack, buffer, packedLight, packedOverlay, 255, color, color, 255);
            }
        }
        poseStack.popPose();
    }
}
