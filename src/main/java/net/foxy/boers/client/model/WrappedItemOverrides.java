package net.foxy.boers.client.model;

import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.data.BoerHead;
import net.foxy.boers.item.BoerContents;
import net.foxy.boers.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class WrappedItemOverrides extends ItemOverrides {
    private static final Map<Direction, List<BakedQuad>> quadsMap = Map.of(
            Direction.DOWN, List.of(),
            Direction.UP, List.of(),
            Direction.NORTH, List.of(),
            Direction.SOUTH, List.of(),
            Direction.WEST, List.of(),
            Direction.EAST, List.of()
    );

    public WrappedItemOverrides() {

    }

    @Override
    public @Nullable BakedModel resolve(BakedModel model, ItemStack bore, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (model instanceof BoerModel.Baked boerModel) {
            ItemStack stack = Utils.getBoerContentsOrEmpty(bore).itemsCopy();
            if (stack.isEmpty()) {
                stack = bore;
            }
            BoerHead head = Utils.getBoer(stack);
            final ResourceLocation texture;
            if (head == null) {
                texture = Utils.rl("item/boer/default_boer_head_idle");
            } else if (!Utils.isUsed(bore)) {
                texture = head.texture().idle();
            } else {
                texture = head.texture().active();
            }
            return boerModel.children.computeIfAbsent(texture, key -> {
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(key);
                if (key == null) {
                    return new SimpleModel(List.of(), sprite, boerModel.getTransforms());
                }

                var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, sprite, null);
                var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, BlockModelRotation.X0_Y0);
                return new SimpleModel(quads, sprite, boerModel.getTransforms());
            });
        }

        return model;
    }
}
