package net.foxy.boers.datagen;

import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.data.BoreColoring;
import net.foxy.boers.data.BoreHead;
import net.foxy.boers.data.StackSmithingTransformRecipeBuilder;
import net.foxy.boers.util.Utils;
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
        SpecialRecipeBuilder.special(BoreColoring::new).save(recipeOutput, Utils.rl("bore_base_coloring"));

        boreHead(recipeOutput,  lookup.holderOrThrow(ModRegistries.COPPER), Items.COPPER_INGOT);
        boreHead(recipeOutput, lookup.holderOrThrow(ModRegistries.DIAMOND), Items.DIAMOND);
        boreHead(recipeOutput, lookup.holderOrThrow(ModRegistries.GOLDEN), Items.GOLD_INGOT);
        boreHead(recipeOutput, lookup.holderOrThrow(ModRegistries.IRON), Items.IRON_INGOT);
        ItemStack result = Utils.bore(lookup.holderOrThrow(ModRegistries.NETHERITE));
        StackSmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), DataComponentIngredient.of(false, ModDataComponents.BORE.get(), lookup.holderOrThrow(ModRegistries.DIAMOND), ModItems.BORE_HEAD), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, result
                )
                .unlocks("has_bore_base", has(ModItems.BORE))
                .save(recipeOutput, Utils.rl("diamond_bore_head_smithing"));
    }

    public static void boreHead(RecipeOutput recipeOutput, Holder<BoreHead> boreHead, Item item) {
        ItemStack stack = Utils.bore(boreHead);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stack).pattern("  X").pattern("XX ").pattern("XX ").define('X', item).unlockedBy("has_bore_base", has(ModItems.BORE)).save(recipeOutput, Utils.rl("bore_head_" + boreHead.getKey().location().getPath()));
    }
}
