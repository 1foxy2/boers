package net.foxy.drills.datagen;

import net.foxy.drills.base.ModDataComponents;
import net.foxy.drills.base.ModItems;
import net.foxy.drills.data.DrillColoring;
import net.foxy.drills.data.DrillHead;
import net.foxy.drills.data.StackSmithingTransformRecipeBuilder;
import net.foxy.drills.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.block.SmithingTableBlock;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        SpecialRecipeBuilder.special(DrillColoring::new).save(recipeOutput, Utils.rl("drill_base_coloring"));

        drillHead(recipeOutput, "copper", Items.COPPER_INGOT);
        drillHead(recipeOutput, "diamond", Items.DIAMOND);
        drillHead(recipeOutput, "golden", Items.GOLD_INGOT);
        drillHead(recipeOutput, "iron", Items.IRON_INGOT);
        ItemStack stack = ModItems.DRILL_HEAD.toStack();
        ItemStack result = ModItems.DRILL_HEAD.toStack();
        result.set(ModDataComponents.DRILL_ID, Utils.rl("netherite"));
        StackSmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), DataComponentIngredient.of(false, ModDataComponents.DRILL_ID.get(), Utils.rl("diamond"), ModItems.DRILL_HEAD), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, result
                )
                .unlocks("has_drill_base", has(ModItems.DRILL_BASE))
                .save(recipeOutput, Utils.rl("diamond_drill_head_smithing"));
    }

    public static void drillHead(RecipeOutput recipeOutput, String drillHead, Item item) {
        ItemStack stack = ModItems.DRILL_HEAD.toStack();
        stack.set(ModDataComponents.DRILL_ID, Utils.rl(drillHead));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stack).pattern("  X").pattern("XX ").pattern("XX ").define('X', item).unlockedBy("has_drill_base", has(ModItems.DRILL_BASE)).save(recipeOutput, Utils.rl("drill_head_" + drillHead));
    }
}
