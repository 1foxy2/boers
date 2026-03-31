package net.foxy.bores.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import net.foxy.bores.base.ModDataComponents;
import net.foxy.bores.base.ModItems;
import net.foxy.bores.util.Utils;

@EmiEntrypoint
public class BoresEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.setDefaultComparison(ModItems.BORE_HEAD.get(), Comparison.compareData(stack -> Utils.getBore(stack.getItemStack())));
    }
}
