package net.foxy.bores.item;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;

import javax.annotation.Nullable;

public final class BoreContents implements TooltipComponent {
    public static final BoreContents EMPTY = new BoreContents(ItemStack.EMPTY);
    public static final Codec<BoreContents> CODEC = ItemStack.CODEC.xmap(BoreContents::new, p_331551_ -> p_331551_.items);
    public static final StreamCodec<RegistryFriendlyByteBuf, BoreContents> STREAM_CODEC = ItemStack.STREAM_CODEC
        .map(BoreContents::new, p_331649_ -> p_331649_.items);
    private static final Fraction BUNDLE_IN_BUNDLE_WEIGHT = Fraction.getFraction(1, 16);
    private static final int NO_STACK_INDEX = -1;
    final ItemStack items;

    public BoreContents(ItemStack items) {
        this.items = items;
    }

    public ItemStack getItemUnsafe() {
        return this.items;
    }

    public ItemStack itemsCopy() {
        return items.copy();
    }

    public int size() {
        return 1;
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return !(other instanceof BoreContents bundlecontents)
                ? false
                : ItemStack.matches(this.items, bundlecontents.items);
        }
    }

    @Override
    public int hashCode() {
        return ItemStack.hashItemAndComponents(this.items);
    }

    @Override
    public String toString() {
        return "BundleContents" + this.items;
    }

    public static class Mutable {
        private ItemStack items;
        private Fraction weight;

        public ItemStack getItems() {
            return items;
        }

        public Mutable(BoreContents contents) {
            this.items = contents.items;
        }

        public Mutable clearItems() {
            this.items = ItemStack.EMPTY;
            return this;
        }

        private int findStackIndex(ItemStack stack) {
            if (!stack.isStackable()) {
                return -1;
            } else {
                    if (ItemStack.isSameItemSameComponents(this.items, stack)) {
                        return 0;
                    }

                return -1;
            }
        }

        private int getMaxAmountToAdd(ItemStack stack) {
            return items.isEmpty() ? 1 : 0;
        }

        public int tryInsert(ItemStack stack) {
            if (!stack.isEmpty() && stack.canFitInsideContainerItems()) { // Neo: stack-aware placeability check
                int i = Math.min(stack.getCount(), this.getMaxAmountToAdd(stack));
                if (i == 0) {
                    return 0;
                } else {
                    int j = this.findStackIndex(stack);
                    if (j != -1) {
                        ItemStack itemstack = this.items;
                        ItemStack itemstack1 = itemstack.copyWithCount(itemstack.getCount() + i);
                        stack.shrink(i);
                        this.items = itemstack1;
                    } else {
                        this.items = stack.split(i);
                    }

                    return i;
                }
            } else {
                return 0;
            }
        }

        public int tryTransfer(Slot slot, Player player) {
            ItemStack itemstack = slot.getItem();
            int i = this.getMaxAmountToAdd(itemstack);
            return this.tryInsert(slot.safeTake(itemstack.getCount(), i, player));
        }

        @Nullable
        public ItemStack removeOne() {
            if (this.items.isEmpty()) {
                return null;
            } else {
                ItemStack itemstack = this.items.copy();
                items = ItemStack.EMPTY;
                return itemstack;
            }
        }

        public Fraction weight() {
            return this.weight;
        }

        public BoreContents toImmutable() {
            return new BoreContents(this.items);
        }
    }
}
