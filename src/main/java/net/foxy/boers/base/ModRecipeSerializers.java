package net.foxy.boers.base;

import net.foxy.boers.BoersMod;
import net.foxy.boers.data.BoerColoring;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {

    public static DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, BoersMod.MODID);

    public static RegistryObject<RecipeSerializer<BoerColoring>> BOER_COLORING = SERIALIZERS.register(
            "crafting_special_boercoloring", () -> new SimpleCraftingRecipeSerializer<>(BoerColoring::new));
}
