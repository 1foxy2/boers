package net.foxy.bores.client.model;

import com.mojang.serialization.MapCodec;
import net.foxy.bores.item.BoreContents;
import net.foxy.bores.util.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public class BoreItemSpecialRenderer implements ItemModel {
    private static final ItemModel INSTANCE = new BoreItemSpecialRenderer();

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
        BoreContents selectedItem = Utils.getBoreContents(item);
        if (selectedItem != null && !selectedItem.isEmpty()) {
            resolver.appendItemLayers(output, selectedItem.getItemUnsafe(), displayContext, level, new BoreItemOwner(Utils.isUsed(item)), seed);
        }
    }

    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<BoreItemSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new BoreItemSpecialRenderer.Unbaked());

        @Override
        public MapCodec<BoreItemSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            return BoreItemSpecialRenderer.INSTANCE;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
        }
    }
}
