package net.foxy.bores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.foxy.bores.base.ModModels;
import net.foxy.bores.client.model.BoreItemOwner;
import net.foxy.bores.item.BoreContents;
import net.foxy.bores.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class BoreSpecialRenderer implements SpecialModelRenderer<DataComponentMap> {

    public final boolean gui;
    public final boolean firstPerson;
    public final ItemStackRenderState state = new ItemStackRenderState();

    public BoreSpecialRenderer(boolean gui ,boolean firstPerson) {
        this.gui = gui;
        this.firstPerson = firstPerson;
    }

    public @Nullable DataComponentMap extractArgument(ItemStack stack) {
        return stack.immutableComponents();
    }

    public void submit(
        @Nullable DataComponentMap components,
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        int lightCoords,
        int overlayCoords,
        boolean hasFoil,
        int outlineColor
    ) {
        poseStack.pushPose();
        if (gui) {
            BoreContents itemStack = Utils.getBoreContents(components);
            boolean flag = itemStack != null && !itemStack.isEmpty();
            if (!flag) {
                poseStack.translate(-1 * 0.0625, 3 * 0.0625, 0);
            }
            submitNodeCollector.submitItem(
                    poseStack,
                    ItemDisplayContext.GUI,
                    lightCoords,
                    overlayCoords,
                    outlineColor,
                    ItemStackRenderState.LayerRenderState.EMPTY_TINTS,
                    Minecraft.getInstance().getModelManager().getStandaloneModel(ModModels.BORES_GUI
                            .get(components.getOrDefault(DataComponents.BASE_COLOR, DyeColor.BLUE))).getAll(),
                    hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE
            );
            poseStack.translate(-1 * 0.0625, 1 * 0.0625, 0.002);

            if (flag) {
                ItemStackRenderState state = new ItemStackRenderState();
                Minecraft.getInstance().getItemModelResolver().updateForTopItem(state, itemStack.getItemUnsafe(), ItemDisplayContext.GUI, null, new BoreItemOwner(Utils.isUsed(components)), 0);
                ItemStackRenderState.LayerRenderState layer = state.layers[0];
               // layer.setItemTransform(ItemTransform.NO_TRANSFORM);
                //layer.setLocalTransform(new Matrix4f().translate(5, 5, 5));
                //poseStack.translate(0.5F, 0.5F, 0.5F);
                //submitNodeCollector.submitItem(poseStack, ItemDisplayContext.NONE, lightCoords, overlayCoords, outlineColor, ItemStackRenderState.LayerRenderState.EMPTY_TINTS, layer.quads, layer.foilType);
                state.submit(poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
            }
        } else {
            submitNodeCollector.submitItem(
                    poseStack,
                    ItemDisplayContext.NONE,
                    lightCoords,
                    overlayCoords,
                    outlineColor,
                    ItemStackRenderState.LayerRenderState.EMPTY_TINTS,
                    Minecraft.getInstance().getModelManager().getStandaloneModel(ModModels.BORES
                            .get(components.getOrDefault(DataComponents.BASE_COLOR, DyeColor.BLUE))).getAll(),
                    hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE
            );
        }
        //ItemStackRenderState output = new ItemStackRenderState();
        //Minecraft.getInstance().getItemModelResolver().updateForTopItem(output, Items.DIAMOND_SWORD.getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, null, null, 0);
        //output.submit(poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        //PoseStack poseStack = new PoseStack();
        //this.model.root().getExtentsForGui(poseStack, output);
    }

    public record Unbaked(boolean gui ,boolean firstPerson) implements SpecialModelRenderer.Unbaked<DataComponentMap> {
        public static final MapCodec<BoreSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.BOOL.fieldOf("gui").forGetter(Unbaked::gui),
                        Codec.BOOL.fieldOf("firstPerson").forGetter(Unbaked::firstPerson)
                ).apply(instance, Unbaked::new)
        );

        @Override
        public MapCodec<BoreSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        public BoreSpecialRenderer bake(BakingContext context) {
            return new BoreSpecialRenderer(gui, firstPerson);
        }
    }
}
