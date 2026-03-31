package net.foxy.bores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.foxy.bores.base.ModModels;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.event.ModClientEvents;
import net.foxy.bores.item.BoreContents;
import net.foxy.bores.util.RenderUtils;
import net.foxy.bores.util.Utils;
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
            RenderUtils.renderItemModel(stack, RenderType.CUTOUT, displayContext, poseStack, buffer, packedLight, packedOverlay, ModModels.BORE_GUI);
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

            if (displayContext.firstPerson()) {
                float angle = Mth.catmullrom(Mth.lerp(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true), ModClientEvents.lastProgress, ModClientEvents.usingProgress) / 10f, 0, 0, 14, 100);
                if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                    poseStack.mulPose(Axis.YN.rotationDegrees(angle));
                } else {
                    poseStack.rotateAround(Axis.YP.rotationDegrees(angle), 0, 0, 1);
                }
            }

            RenderUtils.renderItemModel(stack, NeoForgeRenderTypes.ITEM_LAYERED_CUTOUT.get(), displayContext, poseStack, buffer, packedLight, packedOverlay, ModModels.BORE);
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
