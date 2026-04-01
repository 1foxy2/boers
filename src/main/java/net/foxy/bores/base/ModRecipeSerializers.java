package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.foxy.bores.data.BoreColoring;
import net.foxy.bores.data.BoreSmithingTransformRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {

    public static DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, BoresMod.MODID);

    public static RegistryObject<RecipeSerializer<BoreColoring>> BORE_COLORING = SERIALIZERS.register(
            "crafting_special_bore_coloring", () -> new SimpleCraftingRecipeSerializer<>(BoreColoring::new));
    public static RegistryObject<RecipeSerializer<SmithingTransformRecipe>> BORE_SMITHING = SERIALIZERS.register(
            "smithing_transform", BoreSmithingTransformRecipe.Serializer::new);
}
