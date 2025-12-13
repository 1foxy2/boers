package net.foxy.boers.datagen;

import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.base.ModRecipeSerializers;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.data.BoerColoring;
import net.foxy.boers.data.BoerHead;
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
import net.minecraftforge.common.crafting.PartialNBTIngredient;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    private final CompletableFuture<HolderLookup.Provider> lookup;
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output);
        this.lookup = lookup;
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        lookup.thenAccept(look -> {
            SpecialRecipeBuilder.special(ModRecipeSerializers.BOER_COLORING.get()).save(recipeOutput, "boer_base_coloring");
            HolderLookup.RegistryLookup<BoerHead> registryLookup = look.lookupOrThrow(ModRegistries.BOER_HEAD);
            boerHead(recipeOutput,  registryLookup.getOrThrow(ModRegistries.COPPER), Items.COPPER_INGOT);
            boerHead(recipeOutput, registryLookup.getOrThrow(ModRegistries.DIAMOND), Items.DIAMOND);
            boerHead(recipeOutput, registryLookup.getOrThrow(ModRegistries.GOLDEN), Items.GOLD_INGOT);
            boerHead(recipeOutput, registryLookup.getOrThrow(ModRegistries.IRON), Items.IRON_INGOT);
            ItemStack result = ModItems.BOER_HEAD.get().getDefaultInstance();
            Utils.boer(registryLookup.getOrThrow(ModRegistries.NETHERITE));
            StackSmithingTransformRecipeBuilder.smithing(
                            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), PartialNBTIngredient.of(ModItems.BOER_HEAD.get(), Utils.boerTag(registryLookup.getOrThrow(ModRegistries.DIAMOND))), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, result
                    )
                    .unlocks("has_boer_base", has(ModItems.BOER_BASE.get()))
                    .save(recipeOutput, Utils.rl("diamond_boer_head_smithing"));
        });
    }

    public static void boerHead(Consumer<FinishedRecipe> recipeOutput, Holder.Reference<BoerHead> boerHead, Item item) {
        ItemStack stack = Utils.boer(boerHead);
        //ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stack).pattern("  X").pattern("XX ").pattern("XX ").define('X', item).unlockedBy("has_boer_base", has(ModItems.BOER_BASE)).save(recipeOutput, Utils.rl("boer_head_" + boerHead.getKey().location().getPath()));
    }
}
