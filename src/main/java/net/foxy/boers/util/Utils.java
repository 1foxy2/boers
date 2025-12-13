package net.foxy.boers.util;

import net.foxy.boers.BoersMod;
import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.data.BoerHead;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

public class Utils {
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(BoersMod.MODID, path);
    }


    public static ItemStack boer(Holder<BoerHead> head) {
        ItemStack boer = ModItems.BOER_HEAD.get().getDefaultInstance();
        boer.getOrCreateTag().putString(ModDataComponents.BOER, head.unwrapKey().map(ResourceKey::location).orElseThrow().toString());
        return boer;
    }

    public static CompoundTag boerTag(Holder<BoerHead> head) {
        CompoundTag result = new CompoundTag();
        result.putString(ModDataComponents.BOER, head.unwrapKey().map(ResourceKey::location).orElseThrow().toString());
        return result;
    }

    public static BoerHead getBoer(ItemStack stack) {
        Holder<BoerHead> boerHead = getBoerHolder(stack);

        return boerHead == null ? null : boerHead.value();
    }

    public static Holder<BoerHead> getBoerHolder(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(ModDataComponents.BOER, Tag.TAG_STRING)) {
            return null;
        }

        RegistryAccess access;
        if (FMLEnvironment.dist.isClient()) {
            access = Minecraft.getInstance().level.registryAccess();
        } else {
            access = ServerLifecycleHooks.getCurrentServer().registryAccess();
        }


        return access.lookupOrThrow(ModRegistries.BOER_HEAD).getOrThrow(ResourceKey.create(ModRegistries.BOER_HEAD, ResourceLocation.parse(stack.getTag().getString(ModDataComponents.BOER))));
    }

    public static int getUsedFor(ItemStack stack) {
        if (!stack.hasTag()) {
            return 0;
        }
        return stack.getOrCreateTag().getInt(ModDataComponents.USED_FOR);
    }

    public static void decreaseUseFor(ItemStack stack) {
        stack.getOrCreateTag().putInt(ModDataComponents.USED_FOR, Math.max(0, stack.getOrCreateTag().getInt(ModDataComponents.USED_FOR) - 3));
    }

    public static void increaseUseFor(ItemStack stack) {
        stack.getOrCreateTag().putInt(ModDataComponents.USED_FOR, stack.getTag().getInt(ModDataComponents.USED_FOR) + 1);
    }

    public static boolean isUsed(ItemStack stack) {
        if (!stack.hasTag()) {
            return false;
        }

        return stack.getTag().getBoolean(ModDataComponents.IS_USED);
    }

    public static void setUsed(ItemStack mainHandItem, boolean used) {
        mainHandItem.getOrCreateTag().putBoolean(ModDataComponents.IS_USED, used);
    }

    public static ItemStack getBoerContents(ItemStack stack) {
        return getBoerContentsOrEmpty(stack);
    }

    public static ItemStack getBoerContentsOrEmpty(ItemStack stack) {
        if (stack.getTag() == null) {
            return ItemStack.EMPTY;
        }

        if (!stack.getTag().contains(ModDataComponents.BOER_CONTENTS, Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.of(stack.getTag().getCompound(ModDataComponents.BOER_CONTENTS));
    }

    public static void setBoerContents(ItemStack itemStack, ItemStack boerContents) {
        itemStack.getOrCreateTag().put(ModDataComponents.BOER_CONTENTS, boerContents.save(new CompoundTag()));
    }
}
