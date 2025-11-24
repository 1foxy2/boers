package net.foxy.drills.client.model;

import net.foxy.drills.base.ModDataComponents;
import net.foxy.drills.data.DrillHead;
import net.foxy.drills.item.DrillContents;
import net.foxy.drills.util.Utils;
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
        if (model instanceof DrillModel.Baked drillModel) {
            ItemStack stack = bore.getOrDefault(ModDataComponents.DRILL_CONTENTS, DrillContents.EMPTY).itemsCopy();
            if (stack.isEmpty()) {
                stack = bore;
            }
            DrillHead.Texture head = Utils.drillOrDefault(stack).texture();
            final ResourceLocation texture;
            if (!bore.getOrDefault(ModDataComponents.IS_USED.get(), false)) {
                texture = head.idle();
            } else {
                texture = head.active();
            }
            return drillModel.children.computeIfAbsent(texture, key -> {
                if (key == null) {
                    return new SimpleBakedModel(List.of(), quadsMap, drillModel.useAmbientOcclusion(), drillModel.usesBlockLight(), drillModel.isGui3d(), drillModel.getParticleIcon(), drillModel.getTransforms(), drillModel.getOverrides());
                }
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(key);

                var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, sprite, null);
                var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, BlockModelRotation.X0_Y0);
                return new SimpleBakedModel(quads, quadsMap, drillModel.useAmbientOcclusion(), drillModel.usesBlockLight(), drillModel.isGui3d(), Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture), drillModel.getTransforms(), drillModel.getOverrides());
            });
        }

        return model;
    }
}
