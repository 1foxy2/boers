package net.foxy.bores.datagen;

import net.foxy.bores.base.ModDataComponents;
import net.foxy.bores.base.ModItems;
import net.foxy.bores.base.ModRegistries;
import net.foxy.bores.data.BoerColoring;
import net.foxy.bores.data.BoerHead;
import net.foxy.bores.data.StackSmithingTransformRecipeBuilder;
import net.foxy.bores.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider lookup) {
        SpecialRecipeBuilder.special(BoerColoring::new).save(recipeOutput, Utils.rl("bore_base_coloring"));

        boerHead(recipeOutput,  lookup.holderOrThrow(ModRegistries.COPPER), Items.COPPER_INGOT);
        boerHead(recipeOutput, lookup.holderOrThrow(ModRegistries.DIAMOND), Items.DIAMOND);
        boerHead(recipeOutput, lookup.holderOrThrow(ModRegistries.GOLDEN), Items.GOLD_INGOT);
        boerHead(recipeOutput, lookup.holderOrThrow(ModRegistries.IRON), Items.IRON_INGOT);
        ItemStack result = ModItems.BOER_HEAD.toStack();
        Utils.boer(lookup.holderOrThrow(ModRegistries.NETHERITE));
        StackSmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), DataComponentIngredient.of(false, ModDataComponents.BOER.get(), lookup.holderOrThrow(ModRegistries.DIAMOND), ModItems.BOER_HEAD), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, result
                )
                .unlocks("has_bore_base", has(ModItems.BOER_BASE))
                .save(recipeOutput, Utils.rl("diamond_bore_head_smithing"));
    }

    public static void boerHead(RecipeOutput recipeOutput, Holder<BoerHead> boerHead, Item item) {
        ItemStack stack = Utils.boer(boerHead);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stack).pattern("  X").pattern("XX ").pattern("XX ").define('X', item).unlockedBy("has_bore_base", has(ModItems.BOER_BASE)).save(recipeOutput, Utils.rl("bore_head_" + boerHead.getKey().location().getPath()));
    }
}
