package net.foxy.drills.base;

import net.foxy.drills.DrillsMod;
import net.foxy.drills.data.DrillColoring;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {

    public static DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(BuiltInRegistries.RECIPE_SERIALIZER, DrillsMod.MODID);

    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DrillColoring>> DRILL_COLORING = SERIALIZERS.register(
            "crafting_special_drillcoloring", () -> new SimpleCraftingRecipeSerializer<>(DrillColoring::new));
}
