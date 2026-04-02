package net.foxy.bores.datagen;

import net.foxy.bores.base.ModDataComponents;
import net.foxy.bores.base.ModItems;
import net.foxy.bores.base.ModRegistries;
import net.foxy.bores.data.BoreColoring;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput registries) {
        super(provider, registries);
    }

    @Override
    protected void buildRecipes() {
        SpecialRecipeBuilder.special(BoreColoring::new).save(output, Utils.rl("bore_coloring").toString());

        boreHead(output, registries.holderOrThrow(ModRegistries.COPPER), Items.COPPER_INGOT);
        boreHead(output, registries.holderOrThrow(ModRegistries.DIAMOND), Items.DIAMOND);
        boreHead(output, registries.holderOrThrow(ModRegistries.GOLDEN), Items.GOLD_INGOT);
        boreHead(output, registries.holderOrThrow(ModRegistries.IRON), Items.IRON_INGOT);
        ItemStackTemplate result = new ItemStackTemplate(ModItems.BORE_HEAD, 1, DataComponentPatch.builder().set(ModDataComponents.BORE.get(), registries.holderOrThrow(ModRegistries.NETHERITE)).build());
        new SmithingTransformRecipeBuilder(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), DataComponentIngredient.of(false, ModDataComponents.BORE.get(), registries.holderOrThrow(ModRegistries.DIAMOND), ModItems.BORE_HEAD), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, result
                )
                .unlocks("has_bore", has(ModItems.BORE))
                .save(output, Utils.rl("diamond_bore_head_smithing").toString());
    }

    public void boreHead(RecipeOutput recipeOutput, Holder<BoreHead> boreHead, Item item) {
        ShapedRecipeBuilder.shaped(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.TOOLS,
                new ItemStackTemplate(ModItems.BORE_HEAD.get(),
                        DataComponentPatch.builder().set(ModDataComponents.BORE.get(), boreHead).build()))
                .pattern("  X").pattern("XX ").pattern("XX ").define('X', item)
                .unlockedBy("has_bore", this.has(ModItems.BORE))
                .save(recipeOutput, Utils.rl("bore_head_" + boreHead.getKey().identifier().getPath()).toString());
    }


    public static class Runner extends RecipeProvider.Runner {
        // Get the parameters from GatherDataEvent.
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new ModRecipeProvider(provider, output);
        }

        @Override
        public String getName() {
            return "bores";
        }
    }
}
