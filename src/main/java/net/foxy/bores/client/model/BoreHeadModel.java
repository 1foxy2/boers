package net.foxy.bores.client.model;

import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class BoreHeadModel implements ItemModel {
    private static final ModelDebugName DEBUG_NAME = () -> "BoreHeadModel";
    private final ModelRenderProperties properties;
    private final Matrix4fc transformation;
    public final Map<Identifier, ItemModel> children = new IdentityHashMap<>();
    private final BakingContext bakingContext;

    public BoreHeadModel(ModelRenderProperties properties, Matrix4fc transformation, BakingContext bakingContext) {
        this.properties = properties;
        this.transformation = transformation;
        this.bakingContext = bakingContext;
    }

    public ItemModel bakeModelForSprite(Identifier identifier) {
        if (identifier == null) {
            return new BoreHeadModelWrapper(Collections.emptyList(), QuadCollection.EMPTY, properties, transformation);
        }

        ModelBaker baker = bakingContext.blockModelBaker();
        MaterialBaker materials = baker.materials();
        Material.Baked material = materials.get(new Material(identifier), DEBUG_NAME);
        QuadCollection quads = baker.compute(new ItemModelGenerator.ItemLayerKey(material, BlockModelRotation.IDENTITY, 0));
        return new BoreHeadModelWrapper(
                Collections.emptyList(),
                quads,
                new ModelRenderProperties(false, material, properties.transforms()),
                transformation);
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


        BoreHead head = Utils.getBore(item);
        final Identifier texture;
        if (head == null) {
            texture = Utils.rl("item/bore/default_bore_head_idle");
        } else if (owner instanceof BoreItemOwner(boolean isUsed, int usedFor) && isUsed) {
            texture = head.texture().active();
        } else {
            texture = head.texture().idle();
        }

        children.computeIfAbsent(texture, this::bakeModelForSprite).update(output, item, resolver, displayContext, level, owner, seed);
    }

    public record Unbaked(Identifier model, Optional<Transformation> transformation)
            implements ItemModel.Unbaked {
        public static final MapCodec<BoreHeadModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                i -> i.group(
                                Identifier.CODEC.fieldOf("model").forGetter(BoreHeadModel.Unbaked::model),
                                Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(BoreHeadModel.Unbaked::transformation)
                        )
                        .apply(i, BoreHeadModel.Unbaked::new)
        );

        @Override
        public MapCodec<BoreHeadModel.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            ModelBaker baker = context.blockModelBaker();
            ResolvedModel resolvedModel = baker.getModel(this.model);
            TextureSlots textureSlots = resolvedModel.getTopTextureSlots();
            ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, textureSlots);
            Matrix4fc modelTransform = Transformation.compose(transformation, this.transformation);
            return new BoreHeadModel(properties, modelTransform, context);
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(this.model);
        }
    }
}
