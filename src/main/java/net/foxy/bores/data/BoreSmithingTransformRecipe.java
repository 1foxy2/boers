package net.foxy.bores.data;

import com.google.gson.JsonObject;
import net.foxy.bores.base.ModDataComponents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class BoreSmithingTransformRecipe extends SmithingTransformRecipe {
    public BoreSmithingTransformRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, template, base, addition, result);
    }

    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack itemstack = this.getResultItem(registryAccess).copy();
        CompoundTag compoundtag = container.getItem(1).getTag();
        String head = itemstack.getOrCreateTag().getString(ModDataComponents.BORE);
        if (compoundtag != null) {
            itemstack.setTag(compoundtag.copy());
        }
        itemstack.getTag().putString(ModDataComponents.BORE, head);

        return itemstack;
    }

    public static class Serializer extends SmithingTransformRecipe.Serializer {
        @Override
        public SmithingTransformRecipe fromJson(ResourceLocation p_266953_, JsonObject p_266720_) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getNonNull(p_266720_, "template"));
            Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getNonNull(p_266720_, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(GsonHelper.getNonNull(p_266720_, "addition"));
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_266720_, "result"));
            return new BoreSmithingTransformRecipe(p_266953_, ingredient, ingredient1, ingredient2, itemstack);
        }

        @Override
        public SmithingTransformRecipe fromNetwork(ResourceLocation p_267117_, FriendlyByteBuf p_267316_) {
            Ingredient ingredient = Ingredient.fromNetwork(p_267316_);
            Ingredient ingredient1 = Ingredient.fromNetwork(p_267316_);
            Ingredient ingredient2 = Ingredient.fromNetwork(p_267316_);
            ItemStack itemstack = p_267316_.readItem();
            return new BoreSmithingTransformRecipe(p_267117_, ingredient, ingredient1, ingredient2, itemstack);
        }
    }
}
