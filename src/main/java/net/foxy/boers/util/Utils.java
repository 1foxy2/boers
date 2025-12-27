package net.foxy.boers.util;

import net.foxy.boers.BoersMod;
import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.data.BoerHead;
import net.foxy.boers.item.BoerContents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class Utils {
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(BoersMod.MODID, path);
    }


    public static ItemStack boer(Holder<BoerHead> head) {
        ItemStack boer = ModItems.BOER_HEAD.toStack();
        boer.set(ModDataComponents.BOER, head);
        return boer;
    }

    public static BoerHead getBoer(ItemStack stack) {
        Holder<BoerHead> boerHead = getBoerHolder(stack);

        return boerHead == null ? null : boerHead.value();
    }

    public static Holder<BoerHead> getBoerHolder(ItemStack stack) {
        return stack.get(ModDataComponents.BOER.get());
    }

    public static void setDouble(ItemStack stack, boolean value) {
        if (getDouble(stack) != value) {
            stack.set(ModDataComponents.DOUBLE.get(), value);
        }
    }

    public static boolean getDouble(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.DOUBLE.get(), false);
    }

    public static int getUsedFor(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.USED_FOR, 0);
    }

    public static void decreaseUseFor(ItemStack stack) {
        stack.set(ModDataComponents.USED_FOR, Math.max(0, stack.getOrDefault(ModDataComponents.USED_FOR, 3) - 3));
    }

    public static void increaseUseFor(ItemStack stack) {
        stack.set(ModDataComponents.USED_FOR, stack.getOrDefault(ModDataComponents.USED_FOR, 0) + 1);
    }

    public static boolean isUsed(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.IS_USED, false);
    }

    public static void setUsed(ItemStack mainHandItem, boolean used) {
        mainHandItem.set(ModDataComponents.IS_USED, used);
    }

    public static BoerContents getBoerContents(ItemStack stack) {
        return stack.get(ModDataComponents.BOER_CONTENTS);
    }

    public static BoerContents getBoerContentsOrEmpty(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BOER_CONTENTS, BoerContents.EMPTY);
    }

    public static void setBoerContents(ItemStack itemStack, BoerContents boerContents) {
        itemStack.set(ModDataComponents.BOER_CONTENTS, boerContents);
    }
}
