package net.foxy.bores.client.model;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.foxy.bores.event.ModClientEvents;
import net.foxy.bores.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class BoreModelWrapper implements ItemModel {
    private final ItemTransforms transforms;
    private final ItemModel model;

    public BoreModelWrapper(ItemTransforms transforms, ItemModel model) {
        this.transforms = transforms;
        this.model = model;
    }

    @Override
    public void update(
        ItemStackRenderState output,
        ItemStack item,
        ItemModelResolver resolver,
        ItemDisplayContext displayContext,
        @Nullable ClientLevel level,
        @Nullable ItemOwner owner,
        int seed
    ) {
        output.appendModelIdentityElement(this);
        boolean isUsed = Utils.isUsed(item);
        model.update(output, item, resolver, displayContext, level, new BoreItemOwner(isUsed, Utils.getUsedFor(item)), seed);
        Function<Matrix4f, Matrix4f> matrixModifier = matrix4f -> matrix4f;
        if (displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.FIXED || displayContext == ItemDisplayContext.GROUND) {
            if (Utils.getBoreContentsOrEmpty(item).isEmpty()) {
                matrixModifier = matrix4f -> new Matrix4f(matrix4f).translate(-1f * 0.0625f, 3f * 0.0625f, 0);
            }
        } else if (isUsed) {
            RandomSource randomSource = Minecraft.getInstance().level.getRandom();
            float usedFor = 100f - Math.min(Utils.getUsedFor(item), 100) / 100f;
            float x = randomSource.nextFloat() / usedFor;
            float y = randomSource.nextFloat() / usedFor;
            float z = randomSource.nextFloat() / usedFor;
            if (displayContext.firstPerson()) {
                float angle = Mth.catmullrom(Mth.lerp(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true), ModClientEvents.lastProgress, ModClientEvents.usingProgress) / 10f, 0, 0, 14, 100);
                if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                    matrixModifier = matrix4f -> new Matrix4f(matrix4f).translate(x, y, z).rotate(Axis.YN.rotationDegrees(angle));
                } else {
                    matrixModifier = matrix4f -> new Matrix4f(matrix4f).translate(x, y, z).rotateAround(Axis.YP.rotationDegrees(angle), 0, 0, 1);
                }
            } else {
                matrixModifier = matrix4f -> new Matrix4f(matrix4f).translate(x, y, z);
            }
        } else if (displayContext.firstPerson()) {
            float angle = Mth.catmullrom(Mth.lerp(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true), ModClientEvents.lastProgress, ModClientEvents.usingProgress) / 10f, 0, 0, 14, 100);
            if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                matrixModifier = matrix4f -> new Matrix4f(matrix4f).rotate(Axis.YN.rotationDegrees(angle));
            } else {
                matrixModifier = matrix4f -> new Matrix4f(matrix4f).rotateAround(Axis.YP.rotationDegrees(angle), 0, 0, 1);
            }
        }
        for (ItemStackRenderState.LayerRenderState layer : output.layers) {
            layer.setItemTransform(transforms.getTransform(displayContext));
            layer.setLocalTransform(matrixModifier.apply(layer.localTransform));
            if (!layer.tintLayers().isEmpty()) {
                layer.setLocalTransform(new Matrix4f(layer.localTransform).scale(0.5f, 0.5f, 1.01f).translate(12f * 0.0625f, 5f * 0.0625f, -0.005f));
            }
        }
    }

    public record Unbaked(Identifier modelId, ItemModel.Unbaked model) implements ItemModel.Unbaked {
        public static final MapCodec<BoreModelWrapper.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            Identifier.CODEC.fieldOf("modelId").forGetter(BoreModelWrapper.Unbaked::modelId),
                            ItemModels.CODEC.fieldOf("model").forGetter(BoreModelWrapper.Unbaked::model)
                )
                .apply(i, BoreModelWrapper.Unbaked::new)
        );

        @Override
        public void resolveDependencies(Resolver resolver) {

        }

        @Override
        public ItemModel bake(BakingContext context, Matrix4fc transformation) {
            ModelBaker baker = context.blockModelBaker();
            ResolvedModel resolvedModel = baker.getModel(this.modelId);
            return new BoreModelWrapper(resolvedModel.getTopTransforms(), model.bake(context, transformation));
        }

        @Override
        public MapCodec<BoreModelWrapper.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
