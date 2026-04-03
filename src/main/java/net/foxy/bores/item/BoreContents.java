package net.foxy.bores.item;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;
import java.util.Objects;

public final class BoreContents implements TooltipComponent {
    public static final BoreContents EMPTY = new BoreContents(null);
    public static final Codec<BoreContents> CODEC = ItemStackTemplate.CODEC.xmap(BoreContents::new, BoreContents::getItem);
    public static final StreamCodec<RegistryFriendlyByteBuf, BoreContents> STREAM_CODEC =
            ItemStackTemplate.STREAM_CODEC.map(BoreContents::new, BoreContents::getItem);
    final ItemStackTemplate item;

    public BoreContents(ItemStackTemplate item) {
        this.item = item;
    }

    public ItemStackTemplate getItem() {
        return this.item;
    }

    public ItemStack itemCopy() {
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return item.create();
    }

    public int size() {
        return 1;
    }

    public boolean isEmpty() {
        return this.item == null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return other instanceof BoreContents bundlecontents && Objects.equals(this.item, bundlecontents.item);
        }
    }

    @Override
    public int hashCode() {
        if (item == null) {
            return 0;
        }
        return this.item.hashCode();
    }

    @Override
    public String toString() {
        return "BoreContents" + this.item;
    }

    public static class Mutable {
        private ItemStack item;

        public ItemStack getItem() {
            return item;
        }

        public Mutable(BoreContents contents) {
            this.item = contents.itemCopy();
        }

        public Mutable clearItems() {
            this.item = ItemStack.EMPTY;
            return this;
        }

        private int findStackIndex(ItemStack stack) {
            if (!stack.isStackable()) {
                return -1;
            } else {
                    if (ItemStack.isSameItemSameComponents(this.item, stack)) {
                        return 0;
                    }

                return -1;
            }
        }

        private int getMaxAmountToAdd(ItemStack stack) {
            return item.isEmpty() ? 1 : 0;
        }

        public int tryInsert(ItemStack stack) {
            if (!stack.isEmpty() && stack.getItem().canFitInsideContainerItems()) { // Neo: stack-aware placeability check
                int i = Math.min(stack.getCount(), this.getMaxAmountToAdd(stack));
                if (i == 0) {
                    return 0;
                } else {
                    int j = this.findStackIndex(stack);
                    if (j != -1) {
                        ItemStack itemstack = this.item;
                        ItemStack itemstack1 = itemstack.copyWithCount(itemstack.getCount() + i);
                        stack.shrink(i);
                        this.item = itemstack1;
                    } else {
                        this.item = stack.split(i);
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
            if (this.item.isEmpty()) {
                return null;
            } else {
                ItemStack itemstack = this.item.copy();
                item = ItemStack.EMPTY;
                return itemstack;
            }
        }

        public BoreContents toImmutable() {
            return new BoreContents(item.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(this.item));
        }
    }
}
