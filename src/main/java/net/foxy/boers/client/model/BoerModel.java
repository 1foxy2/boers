/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.foxy.boers.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Forge reimplementation of vanilla's {@link ItemModelGenerator}, i.e. builtin/generated models with some tweaks:
 * - Represented as {@link IUnbakedGeometry} so it can be baked as usual instead of being special-cased
 * - Not limited to an arbitrary number of layers (5)
 * - Support for per-layer render types
 */
public class BoerModel implements IUnbakedGeometry<BoerModel> {
    private ImmutableList<Material> textures;
    private final Int2ObjectMap<ForgeFaceData> layerData;
    private final Int2ObjectMap<ResourceLocation> renderTypeNames;

    private BoerModel(ImmutableList<Material> textures, Int2ObjectMap<ForgeFaceData> layerData, Int2ObjectMap<ResourceLocation> renderTypeNames) {
        this.textures = textures;
        this.layerData = layerData;
        this.renderTypeNames = renderTypeNames;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        if (textures == null) {
            ImmutableList.Builder<Material> builder = ImmutableList.builder();
            for (int i = 0; context.hasMaterial("layer" + i); i++) {
                builder.add(context.getMaterial("layer" + i));
            }
            textures = builder.build();
        }

        TextureAtlasSprite particle = spriteGetter.apply(
                context.hasMaterial("particle") ? context.getMaterial("particle") : textures.get(0));
        var rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity())
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);

        Baked.Builder builder = Baked.builder(context, particle, overrides, context.getTransforms());
        return builder.build();
    }

    public static final class Loader implements IGeometryLoader<BoerModel> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public BoerModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            var renderTypeNames = new Int2ObjectOpenHashMap<ResourceLocation>();
            if (jsonObject.has("render_types")) {
                var renderTypes = jsonObject.getAsJsonObject("render_types");
                for (Map.Entry<String, JsonElement> entry : renderTypes.entrySet()) {
                    var renderType = ResourceLocation.parse(entry.getKey());
                    for (var layer : entry.getValue().getAsJsonArray())
                        if (renderTypeNames.put(layer.getAsInt(), renderType) != null)
                            throw new JsonParseException("Registered duplicate render type for layer " + layer);
                }
            }

            var emissiveLayers = new Int2ObjectArrayMap<ForgeFaceData>();
            if (jsonObject.has("forge_data")) throw new JsonParseException("forge_data should be replaced by neoforge_data"); // TODO 1.22: Remove
            if (jsonObject.has("neoforge_data")) {
                JsonObject forgeData = jsonObject.get("neoforge_data").getAsJsonObject();
                readLayerData(forgeData, "layers", renderTypeNames, emissiveLayers, false);
            }
            return new BoerModel(null, emissiveLayers, renderTypeNames);
        }

        protected void readLayerData(JsonObject jsonObject, String name, Int2ObjectOpenHashMap<ResourceLocation> renderTypeNames, Int2ObjectMap<ForgeFaceData> layerData, boolean logWarning) {
            if (!jsonObject.has(name)) {
                return;
            }
            var fullbrightLayers = jsonObject.getAsJsonObject(name);
            for (var entry : fullbrightLayers.entrySet()) {
                int layer = Integer.parseInt(entry.getKey());
                var data = ForgeFaceData.read(entry.getValue(), ForgeFaceData.DEFAULT);
                layerData.put(layer, data);
            }
        }
    }


    public static class Baked implements IDynamicBakedModel {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemOverrides overrides;
        private final ItemTransforms transforms;
        public final HashMap<ResourceLocation, BakedModel> children = new HashMap<>();

        public Baked(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides, ImmutableMap<String, List<BakedQuad>> children) {
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.overrides = new WrappedItemOverrides();
            this.transforms = transforms;
        }


        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
            return List.of();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return isAmbientOcclusion;
        }

        @Override
        public boolean isGui3d() {
            return isGui3d;
        }

        @Override
        public boolean usesBlockLight() {
            return isSideLit;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return particle;
        }

        @Override
        public ItemOverrides getOverrides() {
            return overrides;
        }

        @Override
        public ItemTransforms getTransforms() {
            return transforms;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
            return ChunkRenderTypeSet.of(ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        }

        @Override
        public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
            return List.of(this) ;
        }

        public static Builder builder(IGeometryBakingContext owner, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms) {
            return builder(owner.useAmbientOcclusion(), owner.isGui3d(), owner.useBlockLight(), particle, overrides, cameraTransforms);
        }

        public static Builder builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms) {
            return new Builder(isAmbientOcclusion, isGui3d, isSideLit, particle, overrides, cameraTransforms);
        }

        public static class Builder {
            private final boolean isAmbientOcclusion;
            private final boolean isGui3d;
            private final boolean isSideLit;
            private final Map<String, List<BakedQuad>> children = new HashMap<>();
            private final ItemOverrides overrides;
            private final ItemTransforms transforms;
            private TextureAtlasSprite particle;

            private Builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms transforms) {
                this.isAmbientOcclusion = isAmbientOcclusion;
                this.isGui3d = isGui3d;
                this.isSideLit = isSideLit;
                this.particle = particle;
                this.overrides = overrides;
                this.transforms = transforms;
            }

            public Builder setParticle(TextureAtlasSprite particleSprite) {
                this.particle = particleSprite;
                return this;
            }

            public Builder addQuads(String texture, List<BakedQuad> quadsToAdd) {
                children.put(texture, quadsToAdd);
                return this;
            }

            public BakedModel build() {
                var childrenBuilder = ImmutableMap.<String, List<BakedQuad>>builder();
                var itemPassesBuilder = ImmutableList.<BakedModel>builder();
                int i = 0;
                for (var model : this.children.entrySet()) {
                    childrenBuilder.put(model.getKey(), model.getValue());
                }
                return new Baked(isGui3d, isSideLit, isAmbientOcclusion, particle, transforms, overrides, childrenBuilder.build());
            }
        }
    }
}
