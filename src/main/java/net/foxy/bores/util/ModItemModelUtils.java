package net.foxy.bores.util;

import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import org.joml.Vector3fc;

public class ModItemModelUtils {

    public static QuadCollection bake(Material.Baked material) {
        QuadCollection.Builder builder = new QuadCollection.Builder();
        ModelBaker.Interner fakeInterner = new ModelBaker.Interner() {
            @Override
            public Vector3fc vector(Vector3fc vector) {
                return vector;
            }

            @Override
            public BakedQuad.MaterialInfo materialInfo(BakedQuad.MaterialInfo material) {
                return material;
            }
        };
        BakedQuad.MaterialInfo materialInfo = fakeInterner
                .materialInfo(BakedQuad.MaterialInfo.of(material,
                        material.sprite().transparency(), 0, true, 0));
        ItemModelGenerator.bakeExtrudedSprite(builder, fakeInterner, BlockModelRotation.IDENTITY, materialInfo);
        return builder.build();
    }
}
