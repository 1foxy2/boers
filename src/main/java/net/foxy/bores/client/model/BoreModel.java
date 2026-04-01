/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.foxy.bores.client.model;

/*import com.google.common.collect.ImmutableList;
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
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BoreModel implements IUnbakedGeometry<BoreModel> {
    @Nullable
    private ImmutableList<Material> textures;
    private final Int2ObjectMap<ExtraFaceData> layerData;
    private final Int2ObjectMap<Identifier> renderTypeNames;

    private BoreModel(@Nullable ImmutableList<Material> textures, Int2ObjectMap<ExtraFaceData> layerData, Int2ObjectMap<Identifier> renderTypeNames) {
        this.textures = textures;
        this.layerData = layerData;
        this.renderTypeNames = renderTypeNames;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, List<ItemOverride> overrides) {
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

        Baked.Builder builder = Baked.builder(context, particle, context.getTransforms());
        return builder.build();
    }

    public static final class Loader implements IGeometryLoader<BoreModel> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public BoreModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            var renderTypeNames = new Int2ObjectOpenHashMap<Identifier>();
            if (jsonObject.has("render_types")) {
                var renderTypes = jsonObject.getAsJsonObject("render_types");
                for (Map.Entry<String, JsonElement> entry : renderTypes.entrySet()) {
                    var renderType = Identifier.parse(entry.getKey());
                    for (var layer : entry.getValue().getAsJsonArray())
                        if (renderTypeNames.put(layer.getAsInt(), renderType) != null)
                            throw new JsonParseException("Registered duplicate render type for layer " + layer);
                }
            }

            var emissiveLayers = new Int2ObjectArrayMap<ExtraFaceData>();
            if (jsonObject.has("forge_data")) throw new JsonParseException("forge_data should be replaced by neoforge_data"); // TODO 1.22: Remove
            if (jsonObject.has("neoforge_data")) {
                JsonObject forgeData = jsonObject.get("neoforge_data").getAsJsonObject();
                readLayerData(forgeData, "layers", renderTypeNames, emissiveLayers, false);
            }
            return new BoreModel(null, emissiveLayers, renderTypeNames);
        }

        private void readLayerData(JsonObject jsonObject, String name, Int2ObjectOpenHashMap<Identifier> renderTypeNames, Int2ObjectMap<ExtraFaceData> layerData, boolean logWarning) {
            if (!jsonObject.has(name)) {
                return;
            }
            var fullbrightLayers = jsonObject.getAsJsonObject(name);
            for (var entry : fullbrightLayers.entrySet()) {
                int layer = Integer.parseInt(entry.getKey());
                var data = ExtraFaceData.read(entry.getValue(), ExtraFaceData.DEFAULT);
                layerData.put(layer, data);
            }
        }
    }


    public static class Baked implements IDynamicBakedModel {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemTransforms transforms;

        public Baked(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ItemTransforms transforms, ImmutableMap<String, List<BakedQuad>> children) {
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
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
        public TextureAtlasSprite getParticleIcon() {
            return particle;
        }

        @Override
        public ItemTransforms getTransforms() {
            return transforms;
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
            return ChunkRenderTypeSet.of(NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        }

        public static Builder builder(IGeometryBakingContext owner, TextureAtlasSprite particle, ItemTransforms cameraTransforms) {
            return builder(owner.useAmbientOcclusion(), owner.isGui3d(), owner.useBlockLight(), particle, cameraTransforms);
        }

        public static Builder builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemTransforms cameraTransforms) {
            return new Builder(isAmbientOcclusion, isGui3d, isSideLit, particle, cameraTransforms);
        }

        public static class Builder {
            private final boolean isAmbientOcclusion;
            private final boolean isGui3d;
            private final boolean isSideLit;
            private final Map<String, List<BakedQuad>> children = new HashMap<>();
            private final ItemTransforms transforms;
            private TextureAtlasSprite particle;

            private Builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemTransforms transforms) {
                this.isAmbientOcclusion = isAmbientOcclusion;
                this.isGui3d = isGui3d;
                this.isSideLit = isSideLit;
                this.particle = particle;
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
                return new Baked(isGui3d, isSideLit, isAmbientOcclusion, particle, transforms, childrenBuilder.build());
            }
        }
    }
}*/
