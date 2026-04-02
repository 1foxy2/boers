package net.foxy.bores.datagen;

import net.foxy.bores.BoresMod;
import net.foxy.bores.base.ModItems;
import net.foxy.bores.client.BoreSpecialRenderer;
import net.foxy.bores.client.model.BoreHeadModel;
import net.foxy.bores.client.model.BoreItemSpecialRenderer;
import net.foxy.bores.client.model.BoreModelWrapper;
import net.foxy.bores.data.BoreColoring;
import net.foxy.bores.util.Utils;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.CompositeModel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ModModelProvider extends ModelProvider {
    public static final ModelTemplate BORE_TEMPLATE = ModelTemplates.createItem(Utils.rl("template_bore").toString(), TextureSlot.LAYER0);

    public ModModelProvider(PackOutput output) {
        super(output, BoresMod.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        List<SelectItemModel.SwitchCase<DyeColor>> models = new ArrayList<>();
        List<SelectItemModel.SwitchCase<DyeColor>> guiModels = new ArrayList<>();
        for (DyeColor color : BoreColoring.ALLOWED_COLORS) {
            Identifier bore = Utils.rl("item/bore_" + color.getSerializedName());
            BORE_TEMPLATE.create(bore, TextureMapping.layer0(new Material(bore)), itemModels.modelOutput);
            models.add(
                    new SelectItemModel.SwitchCase<>(
                            List.of(color),
                            new CuboidItemModelWrapper.Unbaked(
                                    bore,
                                    Optional.empty(),
                                    Collections.emptyList()
                            )
                    )
            );
            bore = Utils.rl("item/bore_gui_" + color.getSerializedName());
            BORE_TEMPLATE.create(bore, TextureMapping.layer0(new Material(bore)), itemModels.modelOutput);
            guiModels.add(
                    new SelectItemModel.SwitchCase<>(
                            List.of(color),
                            new CuboidItemModelWrapper.Unbaked(
                                    bore,
                                    Optional.empty(),
                                    Collections.emptyList()
                            )
                    )
            );
        }
        itemModels.itemModelOutput.accept(
                ModItems.BORE.get(),
                new BoreModelWrapper.Unbaked(
                        Utils.rl("item/template_bore"),
                        new SelectItemModel.Unbaked(
                                Optional.empty(),
                                new SelectItemModel.UnbakedSwitch<>(
                                        new DisplayContext(),
                                        List.of(
                                                new SelectItemModel.SwitchCase<>(
                                                        List.of(ItemDisplayContext.GUI, ItemDisplayContext.FIXED, ItemDisplayContext.GROUND),
                                                        new CompositeModel.Unbaked(
                                                                List.of(
                                                                        new SelectItemModel.Unbaked(
                                                                                Optional.empty(),
                                                                                new SelectItemModel.UnbakedSwitch<>(
                                                                                        new ComponentContents<>(DataComponents.BASE_COLOR),
                                                                                        guiModels
                                                                                ),
                                                                                Optional.of(
                                                                                        new CuboidItemModelWrapper.Unbaked(
                                                                                                Utils.rl("item/bore_gui_blue"),
                                                                                                Optional.empty(),
                                                                                                Collections.emptyList()
                                                                                        )
                                                                                )
                                                                        ),
                                                                        new BoreItemSpecialRenderer.Unbaked(

                                                                        )
                                                                ),
                                                                Optional.empty()
                                                        )
                                                )
                                        )
                                ),
                                Optional.of(
                                        new CompositeModel.Unbaked(
                                                List.of(
                                                        new SelectItemModel.Unbaked(
                                                                Optional.empty(),
                                                                new SelectItemModel.UnbakedSwitch<>(
                                                                        new ComponentContents<>(DataComponents.BASE_COLOR),
                                                                        models
                                                                ),
                                                                Optional.of(
                                                                        new CuboidItemModelWrapper.Unbaked(
                                                                                Utils.rl("item/bore_blue"),
                                                                                Optional.empty(),
                                                                                Collections.emptyList()
                                                                        )
                                                                )
                                                        ),
                                                        new BoreItemSpecialRenderer.Unbaked()
                                                ),
                                                Optional.empty()
                                        )
                                )
                        )

                )
        );
        itemModels.itemModelOutput.accept(
                ModItems.BORE_HEAD.get(),
                new BoreHeadModel.Unbaked(Utils.rl("item/template_bore_head"), Optional.empty())
        );
    }
}
