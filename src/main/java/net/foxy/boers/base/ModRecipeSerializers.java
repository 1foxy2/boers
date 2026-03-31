package net.foxy.boers.base;

import net.foxy.boers.BoresMod;
import net.foxy.boers.data.BoreColoring;
import net.foxy.boers.data.BoreColoring;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {

    public static DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, BoresMod.MODID);

    public static RegistryObject<RecipeSerializer<BoreColoring>> BORE_COLORING = SERIALIZERS.register(
            "crafting_special_borecoloring", () -> new SimpleCraftingRecipeSerializer<>(BoreColoring::new));
}
