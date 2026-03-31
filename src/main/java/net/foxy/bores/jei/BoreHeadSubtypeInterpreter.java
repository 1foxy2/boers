package net.foxy.bores.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.foxy.bores.base.ModDataComponents;
import net.foxy.bores.data.BoreHead;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BoreHeadSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final BoreHeadSubtypeInterpreter INSTANCE = new BoreHeadSubtypeInterpreter();

	private BoreHeadSubtypeInterpreter() {

	}

	@Override
	public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
		return ingredient.get(ModDataComponents.BORE);
	}

	@Override
	public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
		Holder<BoreHead> bore = itemStack.get(ModDataComponents.BORE);
		if (bore == null) {
			return "";
		}
		return bore.getKey().location().toString();
	}
}
