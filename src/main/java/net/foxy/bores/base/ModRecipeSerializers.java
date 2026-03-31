package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.foxy.bores.data.BoreColoring;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {

    public static DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(BuiltInRegistries.RECIPE_SERIALIZER, BoresMod.MODID);

    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BoreColoring>> BORE_COLORING = SERIALIZERS.register(
            "crafting_special_bore_coloring", () -> new SimpleCraftingRecipeSerializer<>(BoreColoring::new));
}
