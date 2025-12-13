package net.foxy.boers.client.model;

import net.foxy.boers.data.BoerHead;
import net.foxy.boers.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class WrappedItemOverrides extends ItemOverrides {

    public final HashMap<ResourceLocation, BakedModel> children = new HashMap<>();

    public WrappedItemOverrides() {

    }

    @Override
    public @Nullable BakedModel resolve(BakedModel model, ItemStack bore, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
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
        return children.computeIfAbsent(texture, key -> {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(key);
            if (key == null) {
                return new SimpleModel(List.of(), sprite, model.getTransforms());
            }

            var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, sprite, null);
            var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, BlockModelRotation.X0_Y0);
            return new SimpleModel(quads, sprite, model.getTransforms());
        });
    }
}
