package net.foxy.bores.item;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class BoreTooltip implements TooltipComponent {
   private final ItemStack items;

   public BoreTooltip(ItemStack items) {
      this.items = items;
   }

   public ItemStack getItems() {
      return this.items;
   }
}
