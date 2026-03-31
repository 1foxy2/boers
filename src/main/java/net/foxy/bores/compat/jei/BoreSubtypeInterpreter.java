package net.foxy.bores.compat.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BoreSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final BoreSubtypeInterpreter INSTANCE = new BoreSubtypeInterpreter();

	private BoreSubtypeInterpreter() {

	}

	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		return ingredient.get(DataComponents.BASE_COLOR);
	}

	@Override
	public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
		DyeColor color = itemStack.get(DataComponents.BASE_COLOR);
		if (color == null) {
			return "";
		}
		return color.getName();
	}
}
