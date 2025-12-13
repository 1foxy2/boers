package net.foxy.boers.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleModel implements BakedModel {
    public static final List<BakedQuad> empty = List.of();
    public final List<BakedQuad> quads;
    public final TextureAtlasSprite sprite;
    public final ItemTransforms itemTransforms;

    public SimpleModel(List<BakedQuad> quads, TextureAtlasSprite sprite, ItemTransforms itemTransforms) {
        this.quads = quads;
        this.sprite = sprite;
        this.itemTransforms = itemTransforms;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        return side == null ? quads : empty;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return getQuads(state, direction, random, ModelData.EMPTY, null);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public ItemTransforms getTransforms() {
        return itemTransforms;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return sprite;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
