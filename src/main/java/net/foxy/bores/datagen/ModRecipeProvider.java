package net.foxy.bores.datagen;

import net.foxy.bores.base.ModItems;
import net.foxy.bores.base.ModRecipeSerializers;
import net.foxy.bores.base.ModRegistries;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.data.NbtShapedRecipeBuilder;
import net.foxy.bores.data.StackSmithingTransformRecipeBuilder;
import net.foxy.bores.util.Utils;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        SpecialRecipeBuilder.special(ModRecipeSerializers.BORE_COLORING.get()).save(recipeOutput, "bore_base_coloring");
        boerHead(recipeOutput, ModRegistries.COPPER, Items.COPPER_INGOT);
        boerHead(recipeOutput, ModRegistries.DIAMOND, Items.DIAMOND);
        boerHead(recipeOutput, ModRegistries.GOLDEN, Items.GOLD_INGOT);
        boerHead(recipeOutput, ModRegistries.IRON, Items.IRON_INGOT);
        ItemStack result = Utils.bore(ModRegistries.NETHERITE);
        StackSmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), PartialNBTIngredient.of(ModItems.BORE_HEAD.get(), Utils.boreTag(ModRegistries.DIAMOND)), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, result
                )
                .unlocks("has_boer_base", has(ModItems.BORE.get()))
                .save(recipeOutput, Utils.rl("diamond_boer_head_smithing"));
    }

    public static void boerHead(Consumer<FinishedRecipe> recipeOutput, ResourceKey<BoreHead> boerHead, Item item) {
        ItemStack stack = Utils.bore(boerHead);
        NbtShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stack).pattern("  X").pattern("XX ").pattern("XX ").define('X', item).unlockedBy("has_boer_base", has(ModItems.BORE.get())).save(recipeOutput, Utils.rl("boer_head_" + boerHead.location().getPath()));
    }
}
