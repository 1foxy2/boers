package net.foxy.boers.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;

import java.util.List;

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

    public static void renderItemModel(ItemStack bore, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, int red, int green, int blue, int alpha) {
        boolean flag1;
        ItemStack stack = Utils.getBoerContentsOrEmpty(bore).getItemUnsafe();
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
                vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, RenderType.CUTOUT, true, stack.hasFoil());
            } else {
                vertexconsumer = ItemRenderer.getFoilBuffer(buffer, RenderType.CUTOUT, true, stack.hasFoil());
            }

            renderModelLists(model, stack, packedLight, packedOverlay, poseStack, vertexconsumer, red, green, blue, alpha);
        }
    }

    public static void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack poseStack, VertexConsumer buffer, int red, int green, int blue, int alpha) {
        RandomSource randomsource = RandomSource.create();
        long i = 42L;

        for (Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            renderQuadList(poseStack, buffer, model.getQuads(null, direction, randomsource), stack, combinedLight, combinedOverlay, red, green, blue, alpha);
        }

        randomsource.setSeed(42L);
        renderQuadList(poseStack, buffer, model.getQuads(null, null, randomsource), stack, combinedLight, combinedOverlay, red, green, blue, alpha);
    }

    public static void renderQuadList(PoseStack poseStack, VertexConsumer buffer, List<BakedQuad> quads, ItemStack itemStack, int combinedLight, int combinedOverlay, int red, int green, int blue, int alpha) {
        boolean flag = !itemStack.isEmpty();
        PoseStack.Pose posestack$pose = poseStack.last();

        for (BakedQuad bakedquad : quads) {
            int i = -1;

            float f = (float) alpha / 255.0F;
            float f1 = (float)red / 255.0F;
            float f2 = (float)green / 255.0F;
            float f3 = (float)blue / 255.0F;
            buffer.putBulkData(posestack$pose, bakedquad, f1, f2, f3, f, combinedLight, combinedOverlay, true); // Neo: pass readExistingColor=true
        }
    }
}
