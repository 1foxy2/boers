package net.foxy.boers.item;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class BoerTooltip implements TooltipComponent {
   private final ItemStack items;

   public BoerTooltip(ItemStack items) {
      this.items = items;
   }

   public ItemStack getItems() {
      return this.items;
   }
}
