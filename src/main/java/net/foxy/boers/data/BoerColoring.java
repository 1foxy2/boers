package net.foxy.boers.data;

import net.foxy.boers.base.ModRecipeSerializers;
import net.foxy.boers.item.BoerBaseItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class BoerColoring extends CustomRecipe {
    public static final List<DyeColor> ALLOWED_COLORS = List.of(
            DyeColor.WHITE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.GRAY,
            DyeColor.GREEN,
            DyeColor.RED,
            DyeColor.BLACK,
            DyeColor.BLUE
    );

    public BoerColoring(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int i = 0;
        int j = 0;

        for (int k = 0; k < container.getContainerSize(); k++) {
            ItemStack itemstack = container.getItem(k);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof BoerBaseItem) {
                    i++;
                } else {
                    if (!(itemstack.getItem() instanceof DyeItem dye) || !ALLOWED_COLORS.contains(dye.getDyeColor())) {
                        return false;
                    }

                    j++;
                }

                if (j > 1 || i > 1) {
                    return false;
                }
            }
        }

        return i == 1 && j == 1;
    }


    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        ItemStack itemstack = ItemStack.EMPTY;
        net.minecraft.world.item.DyeColor dyecolor = net.minecraft.world.item.DyeColor.WHITE;

        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack itemstack1 = input.getItem(i);
            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();
                if (item instanceof BoerBaseItem) {
                    itemstack = itemstack1.copy();
                } else {
                    net.minecraft.world.item.DyeColor tmp = net.minecraft.world.item.DyeColor.getColor(itemstack1);
                    if (tmp != null) dyecolor = tmp;
                }
            }
        }

        itemstack.getOrCreateTag().putInt("color", dyecolor.getId());
        return itemstack;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BOER_COLORING.get();
    }
}
