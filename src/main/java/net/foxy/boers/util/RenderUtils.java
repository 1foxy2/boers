package net.foxy.boers.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;

public class RenderUtils {
    public static void renderItemModel(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, ModelResourceLocation modelLoc) {
        boolean flag1;
        if (displayContext != ItemDisplayContext.GUI && !displayContext.firstPerson() && stack.getItem() instanceof BlockItem blockitem) {
            Block block = blockitem.getBlock();
            flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
        } else {
            flag1 = true;
        }

        BakedModel bakedModel = Minecraft.getInstance().getModelManager().getModel(modelLoc);
        bakedModel = bakedModel.getOverrides().resolve(bakedModel, stack, Minecraft.getInstance().level, null, 0);
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        for (var model : bakedModel.getRenderPasses(stack, flag1)) {
            VertexConsumer vertexconsumer;
            if (flag1) {
                vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, RenderType.cutout(), true, stack.hasFoil());
            } else {
                vertexconsumer = ItemRenderer.getFoilBuffer(buffer, RenderType.cutout(), true, stack.hasFoil());
            }

            renderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, vertexconsumer);
        }
    }

    public static void renderItemModel(ItemStack bore, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        boolean flag1;
        ItemStack stack = Utils.getBoerContentsOrEmpty(bore);
        if (stack.isEmpty()) {
            stack = bore;
        }
        if (displayContext != ItemDisplayContext.GUI && !displayContext.firstPerson() && stack.getItem() instanceof BlockItem blockitem) {
            Block block = blockitem.getBlock();
            flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
        } else {
            flag1 = true;
        }

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel =  renderer.getItemModelShaper().getItemModel(stack);
        bakedModel = bakedModel.getOverrides().resolve(bakedModel, bore, Minecraft.getInstance().level, null, 0);
        for (var model : bakedModel.getRenderPasses(stack, flag1)) {
            VertexConsumer vertexconsumer;
            if (flag1) {
                vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, RenderType.cutout(), true, stack.hasFoil());
            } else {
                vertexconsumer = ItemRenderer.getFoilBuffer(buffer, RenderType.cutout(), true, stack.hasFoil());
            }

            renderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, vertexconsumer);
        }
    }
}
