package net.foxy.boers.datagen;

import com.mojang.logging.LogUtils;
import net.foxy.boers.BoersMod;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.data.BoerColoring;
import net.foxy.boers.util.Utils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BoersMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        LogUtils.getLogger().warn("testu");
        entity(ModItems.BOER_BASE).transforms()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(-72.5f, -16, -27).translation(-5.325f, 5, -2.25f).scale(1, 1, 0.5f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(107.5f, 16, -153).translation(-5.325f, 5, -2.25f).scale(1, 1, 0.5f).end()
                .end();

        // right 110 -16 153 left -70 16 27
        ItemModelBuilder.OverrideBuilder override = tool(ModItems.BOER_BASE).override();
        ItemModelBuilder.OverrideBuilder overrideGui = tool("boer_base_gui", "boer_base_gui").override();

        for (int j = 0; j < 7; j++) {
            DyeColor dyeColor = BoerColoring.ALLOWED_COLORS.get(j);
            override.predicate(Utils.rl("color"), dyeColor.getId()).model(tool("boer_base_" + dyeColor.getName()));
            overrideGui.predicate(Utils.rl("color"), dyeColor.getId()).model(tool("boer_base_gui_" + dyeColor.getName()));
            if (j == 6) {
                override.end();
                overrideGui.end();
            } else {
                override = override.end().override();
                overrideGui = overrideGui.end().override();
            }
        }
    }

    private ItemModelBuilder entity(RegistryObject<? extends ItemLike> item) {
        return withExistingParent(item.getId().getPath(), Utils.rl("item/entity"));
    }

    private ItemModelBuilder simpleItem(RegistryObject<? extends ItemLike> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                Utils.rl("item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleItem(RegistryObject<? extends ItemLike> item, String string) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated"))
                .texture("layer1", Utils.rl("item/" + item.getId().getPath()))
                .texture("layer0", Utils.rl("item/" + string))
                ;
    }


    private ItemModelBuilder tool(RegistryObject<? extends Item> item) {
        return tool(item, item.getId().getPath() + "_texture");
    }
    private ItemModelBuilder tool(RegistryObject<? extends Item> item, String name) {
        return tool(name, item.getId().getPath());
    }

    private ItemModelBuilder tool(String name) {
        return tool(name, name);
    }

    private ItemModelBuilder tool(String name, String texture) {
        return withExistingParent(name,
                ResourceLocation.parse("minecraft:item/handheld"))
                .texture("layer0",
                        Utils.rl("item/" + texture)).renderType("cutout");
    }

    private ItemModelBuilder tool(RegistryObject<? extends Item> item, int layers) {
        var model = withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("minecraft:item/handheld"))
                .texture("layer0",
                        Utils.rl("item/" + item.getId().getPath()));

        for (int i = 0; i < layers; i++) {
            model.texture("layer" + (i + 1),
                    Utils.rl("item/" + item.getId().getPath() + (i + 1)));
        }

        return model;
    }

    private ItemModelBuilder blank(RegistryObject<? extends Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                Utils.rl("item/blank"));
    }
    private ItemModelBuilder customTexture(RegistryObject<? extends Item> item, String texture) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                Utils.rl("item/" + texture));
    }
    private ItemModelBuilder simpleBlockItem(RegistryObject<? extends ItemLike> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                Utils.rl("block/" + item.getId().getPath()));
    }
}
