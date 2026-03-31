package net.foxy.boers.util;

import net.foxy.boers.BoresMod;
import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.data.BoreHead;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.BiConsumer;

public class Utils {
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(BoresMod.MODID, path);
    }


    public static ItemStack bore(Holder<BoreHead> head) {
        ItemStack bore = ModItems.BORE_HEAD.get().getDefaultInstance();
        bore.getOrCreateTag().putString(ModDataComponents.BORE, head.unwrapKey().map(ResourceKey::location).orElseThrow().toString());
        return bore;
    }

    public static ItemStack bore(ResourceKey<BoreHead> head) {
        ItemStack bore = ModItems.BORE_HEAD.get().getDefaultInstance();
        bore.getOrCreateTag().putString(ModDataComponents.BORE, head.location().toString());
        return bore;
    }

    public static CompoundTag boreTag(Holder<BoreHead> head) {
        CompoundTag result = new CompoundTag();
        result.putString(ModDataComponents.BORE, head.unwrapKey().map(ResourceKey::location).orElseThrow().toString());
        return result;
    }

    public static CompoundTag boreTag(ResourceKey<BoreHead> head) {
        CompoundTag result = new CompoundTag();
        result.putString(ModDataComponents.BORE, head.location().toString());
        return result;
    }

    public static BoreHead getBore(ItemStack stack) {
        Holder<BoreHead> boreHead = getBoreHolder(stack);

        return boreHead == null ? null : boreHead.value();
    }

    public static Holder<BoreHead> getBoreHolder(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(ModDataComponents.BORE, Tag.TAG_STRING)) {
            return null;
        }

        RegistryAccess access;
        if (FMLEnvironment.dist.isClient()) {
            access = Minecraft.getInstance().level.registryAccess();
        } else {
            access = ServerLifecycleHooks.getCurrentServer().registryAccess();
        }


        return access.lookupOrThrow(ModRegistries.BORE_HEAD).get(ResourceKey.create(ModRegistries.BORE_HEAD, ResourceLocation.parse(stack.getTag().getString(ModDataComponents.BORE)))).orElse(null);
    }

    public static void setDouble(ItemStack stack, boolean value) {
        if (getDouble(stack) != value) {
            stack.getOrCreateTag().putBoolean(ModDataComponents.DOUBLE, value);
        }
    }

    public static boolean getDouble(ItemStack stack) {
        if (!stack.hasTag()) {
            return false;
        }

        return stack.getTag().getBoolean(ModDataComponents.DOUBLE);
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

    public static ItemStack getBoreContents(ItemStack stack) {
        return getBoreContentsOrEmpty(stack);
    }

    public static ItemStack getBoreContentsOrEmpty(ItemStack stack) {
        if (stack.getTag() == null) {
            return ItemStack.EMPTY;
        }

        if (!stack.getTag().contains(ModDataComponents.BORE_CONTENTS, Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.of(stack.getTag().getCompound(ModDataComponents.BORE_CONTENTS));
    }

    public static void setBoreContents(ItemStack itemStack, ItemStack boreContents) {
        itemStack.getOrCreateTag().put(ModDataComponents.BORE_CONTENTS, boreContents.save(new CompoundTag()));
    }

    public static void forEachBlock(Level level, Player player, BlockPos pos,
                                    Vec3i radius, BiConsumer<BlockPos, BlockState> action) {
        BlockPos startPos;
        Direction direction = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE).getDirection();
        switch (direction) {
            case DOWN -> {
                radius = new Vec3i(radius.getX(), radius.getZ(), radius.getY());
                startPos = pos.offset(-radius.getX(), 0, -radius.getZ());
            }
            case UP -> {
                radius = new Vec3i(radius.getX(), radius.getZ(), radius.getY());
                startPos = pos.offset(-radius.getX(), -radius.getY() * 2, -radius.getZ());
            }
            case NORTH -> {
                startPos = pos.offset(-radius.getX(), Math.max(-radius.getY(), -1), 0);
            }
            case SOUTH -> {
                startPos = pos.offset(-radius.getX(), Math.max(-radius.getY(), -1), -radius.getZ() * 2);
            }
            case WEST -> {
                radius = new Vec3i(radius.getZ(), radius.getY(), radius.getX());
                startPos = pos.offset(0, Math.max(-radius.getY(), -1), -radius.getZ());
            }
            default -> {
                radius = new Vec3i(radius.getZ(), radius.getY(), radius.getX());
                startPos = pos.offset(-radius.getX() * 2, Math.max(-radius.getY(), -1), -radius.getZ());
            }
        }
        for (int x = 0; x < radius.getX() * 2 + 1; x++) {
            for (int y = 0; y < radius.getY() * 2 + 1; y++) {
                for (int z = 0; z < radius.getZ() * 2 + 1; z++) {
                    BlockPos target = startPos.offset(x, y, z);
                    if (!target.equals(pos)) {
                        BlockState block = level.getBlockState(target);
                        if (!block.isAir() && block.getDestroySpeed(level, target) >= 0 && block.canHarvestBlock(level, target, player)) {
                            action.accept(target, block);
                        }
                    }
                }
            }
        }
    }


    public static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluidMode) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vec3 = player.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttributeValue(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get()) + 0.5; // Block reach is 4.5, vanilla uses 5.0 here, so add 0.5 padding
        Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, fluidMode, player));
    }
}
