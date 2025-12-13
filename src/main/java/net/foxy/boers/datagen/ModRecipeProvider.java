package net.foxy.boers.datagen;

import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.base.ModRecipeSerializers;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.data.BoerColoring;
import net.foxy.boers.data.BoerHead;
import net.foxy.boers.data.NbtShapedRecipeBuilder;
import net.foxy.boers.data.StackSmithingTransformRecipeBuilder;
import net.foxy.boers.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        SpecialRecipeBuilder.special(ModRecipeSerializers.BOER_COLORING.get()).save(recipeOutput, "boer_base_coloring");
        boerHead(recipeOutput, ModRegistries.COPPER, Items.COPPER_INGOT);
        boerHead(recipeOutput, ModRegistries.DIAMOND, Items.DIAMOND);
        boerHead(recipeOutput, ModRegistries.GOLDEN, Items.GOLD_INGOT);
        boerHead(recipeOutput, ModRegistries.IRON, Items.IRON_INGOT);
        ItemStack result = Utils.boer(ModRegistries.NETHERITE);
        StackSmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), PartialNBTIngredient.of(ModItems.BOER_HEAD.get(), Utils.boerTag(ModRegistries.DIAMOND)), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, result
                )
                .unlocks("has_boer_base", has(ModItems.BOER_BASE.get()))
                .save(recipeOutput, Utils.rl("diamond_boer_head_smithing"));
    }

    public static void boerHead(Consumer<FinishedRecipe> recipeOutput, ResourceKey<BoerHead> boerHead, Item item) {
        ItemStack stack = Utils.boer(boerHead);
        NbtShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stack).pattern("  X").pattern("XX ").pattern("XX ").define('X', item).unlockedBy("has_boer_base", has(ModItems.BOER_BASE.get())).save(recipeOutput, Utils.rl("boer_head_" + boerHead.location().getPath()));
    }
}
