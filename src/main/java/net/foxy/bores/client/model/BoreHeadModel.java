package net.foxy.bores.client.model;

import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.util.ModItemModelUtils;
import net.foxy.bores.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BoreHeadModel implements ItemModel {
    private final ModelRenderProperties properties;
    private final Matrix4fc transformation;
    public final HashMap<Identifier, ItemModel> children = new HashMap<>();

    public BoreHeadModel(ModelRenderProperties properties, Matrix4fc transformation) {
        this.properties = properties;
        this.transformation = transformation;
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
        } else if (owner instanceof BoreItemOwner(boolean isUsed) && isUsed) {
            texture = head.texture().active();
        } else {
            texture = head.texture().idle();
        }

        children.computeIfAbsent(texture, key -> {
            if (key == null) {
                return new CuboidItemModelWrapper(Collections.emptyList(), QuadCollection.EMPTY, properties, transformation);
            }
            Material.Baked baked = new Material.Baked(Minecraft.getInstance().getAtlasManager()
                    .getAtlasOrThrow(AtlasIds.ITEMS).getSprite(key), false);

            return new CuboidItemModelWrapper(
                    Collections.emptyList(),
                    ModItemModelUtils.bake(baked),
                    new ModelRenderProperties(false, baked, properties.transforms()),
                    transformation
            );
        }).update(output, item, resolver, displayContext, level, owner, seed);
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
            return new BoreHeadModel(properties, modelTransform);
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(this.model);
        }
    }
}
