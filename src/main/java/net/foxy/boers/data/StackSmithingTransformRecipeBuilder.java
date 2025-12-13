package net.foxy.boers.data;

import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StackSmithingTransformRecipeBuilder {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final ItemStack result;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    private final RecipeSerializer<?> type;

    public StackSmithingTransformRecipeBuilder(RecipeSerializer<?> type, Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, ItemStack result) {
        this.category = category;
        this.type = type;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static StackSmithingTransformRecipeBuilder smithing(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, ItemStack result) {
        return new StackSmithingTransformRecipeBuilder(RecipeSerializer.SMITHING_TRANSFORM, template, base, addition, category, result);
    }

    public StackSmithingTransformRecipeBuilder unlocks(String key, CriterionTriggerInstance criterion) {
        this.advancement.addCriterion(key, criterion);
        return this;
    }

    public void save(Consumer<FinishedRecipe> recipeConsumer, String location) {
        this.save(recipeConsumer, ResourceLocation.parse(location));
    }

    public void save(Consumer<FinishedRecipe> recipeConsumer, ResourceLocation location) {
        this.ensureValid(location);
        this.advancement.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location)).rewards(AdvancementRewards.Builder.recipe(location)).requirements(RequirementsStrategy.OR);
        recipeConsumer.accept(new StackSmithingTransformRecipeBuilder.Result(location, this.type, this.template, this.base, this.addition, this.result, this.advancement, location.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation location) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + location);
        }
    }

    public static record Result(ResourceLocation id, RecipeSerializer<?> type, Ingredient template, Ingredient base, Ingredient addition, ItemStack result, Advancement.Builder advancement, ResourceLocation advancementId) implements FinishedRecipe {
        public void serializeRecipeData(JsonObject p_266713_) {
            p_266713_.add("template", this.template.toJson());
            p_266713_.add("base", this.base.toJson());
            p_266713_.add("addition", this.addition.toJson());
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result.getItem()).toString());
            if (result.hasTag()) {
                jsonobject.addProperty("nbt", result.getOrCreateTag().toString());
            }
            p_266713_.add("result", jsonobject);
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public RecipeSerializer<?> getType() {
            return this.type;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
