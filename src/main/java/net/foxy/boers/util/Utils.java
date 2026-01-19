package net.foxy.boers.util;

import net.foxy.boers.BoersMod;
import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.data.BoerHead;
import net.foxy.boers.item.BoerContents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

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

    public static void forEachBlock(Level level, Player player, BlockPos pos,
                                    Vec3i radius, BiConsumer<BlockPos, BlockState> action) {

        ;
        BlockPos startPos;
        Direction direction = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE).getDirection();
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
}
