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
        SpecialRecipeBuilder.special(ModRecipeSerializers.BORE_COLORING.get()).save(recipeOutput, "bores:bore_coloring");
        boreHead(recipeOutput, ModRegistries.COPPER, Items.COPPER_INGOT);
        boreHead(recipeOutput, ModRegistries.DIAMOND, Items.DIAMOND);
        boreHead(recipeOutput, ModRegistries.GOLDEN, Items.GOLD_INGOT);
        boreHead(recipeOutput, ModRegistries.IRON, Items.IRON_INGOT);
        ItemStack result = Utils.bore(ModRegistries.NETHERITE);
        StackSmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), PartialNBTIngredient.of(ModItems.BORE_HEAD.get(), Utils.boreTag(ModRegistries.DIAMOND)), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, result
                )
                .unlocks("has_bore", has(ModItems.BORE.get()))
                .save(recipeOutput, Utils.rl("diamond_bore_head_smithing"));
    }

    public static void boreHead(Consumer<FinishedRecipe> recipeOutput, ResourceKey<BoreHead> boreHead, Item item) {
        ItemStack stack = Utils.bore(boreHead);
        NbtShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stack).pattern("  X").pattern("XX ").pattern("XX ").define('X', item).unlockedBy("has_bore", has(ModItems.BORE.get())).save(recipeOutput, Utils.rl("bore_head_" + boreHead.location().getPath()));
    }
}
