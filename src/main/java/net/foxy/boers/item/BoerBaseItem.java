package net.foxy.boers.item;

import com.mojang.logging.LogUtils;
import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.base.ModParticles;
import net.foxy.boers.base.ModSounds;
import net.foxy.boers.client.BoerSoundInstance;
import net.foxy.boers.client.BoersClientConfig;
import net.foxy.boers.data.BoerHead;
import net.foxy.boers.event.ModClientEvents;
import net.foxy.boers.particle.spark.SparkParticle;
import net.foxy.boers.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class BoerBaseItem extends Item {
    public BoerBaseItem() {
        super(new Properties().stacksTo(1)
                .component(ModDataComponents.BOER_CONTENTS, BoerContents.EMPTY).component(DataComponents.BASE_COLOR, DyeColor.BLUE)
        );
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        ItemStack boerItem = Utils.getBoerContents(stack).getItemUnsafe();
        return !boerItem.isEmpty() && boerItem.isDamageableItem();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected) {
            entity.setYBodyRot(entity.getYHeadRot() + 37);
            if (entity instanceof Player player) {
                if (Utils.isUsed(stack)) {
                    player.swinging = false;
                    player.attackAnim = 0;
                    player.swingTime = 0;
                } else {
                    Utils.decreaseUseFor(stack);
                }
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Utils.getBoerContentsOrEmpty(stack).items.getMaxDamage();
    }

    @Override
    public int getDamage(ItemStack stack) {
        return Utils.getBoerContentsOrEmpty(stack).items.getDamageValue();
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.gameMode.handleBlockBreakAction(serverPlayer.gameMode.destroyPos,
                    ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, Direction.UP, entity.level().getMaxBuildHeight(), 0);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(newStack.getItem());
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        BoerHead tool = Utils.getBoer(Utils.getBoerContentsOrEmpty(stack).items);
        return tool != null ? tool.getMiningSpeed(stack, state) : 1.0F;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !oldStack.is(newStack.getItem());
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        BoerContents boerContents = Utils.getBoerContents(stack);
        if (boerContents == null) {
            return false;
        } else {
            if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
                ItemStack boer = boerContents.getItemUnsafe();
                BoerHead tool = Utils.getBoer(Utils.getBoerContentsOrEmpty(stack).items);
                int damage = tool != null ? tool.getDamage(state) : 1;
                boer.hurtAndBreak(damage, miningEntity, EquipmentSlot.MAINHAND);
                Utils.setBoerContents(stack, new BoerContents(boer));
                if (tool != null && miningEntity instanceof ServerPlayer player) {
                    if (tool.radius().isPresent()) {
                        Vec3i radius;
                        BlockPos startPos;
                        Direction direction = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE).getDirection();
                        switch (direction) {
                            case DOWN -> {
                                radius = new Vec3i(tool.radius().get().getX(), tool.radius().get().getZ(), tool.radius().get().getY());
                                startPos = pos.offset(-radius.getX(), 0, -radius.getZ());
                            }
                            case UP -> {
                                radius = new Vec3i(tool.radius().get().getX(), tool.radius().get().getZ(), tool.radius().get().getY());
                                startPos = pos.offset(-radius.getX(), -radius.getY() * 2, -radius.getZ());
                            }
                            case NORTH -> {
                                radius = tool.radius().get();
                                startPos = pos.offset(-radius.getX(), Math.max(-radius.getY(), -1), 0);
                            }
                            case SOUTH -> {
                                radius = tool.radius().get();
                                startPos = pos.offset(-radius.getX(), Math.max(-radius.getY(), -1), -radius.getZ() * 2);
                            }
                            case WEST -> {
                                radius = new Vec3i(tool.radius().get().getZ(), tool.radius().get().getY(), tool.radius().get().getX());
                                startPos = pos.offset(0, Math.max(-radius.getY(), -1), -radius.getZ());
                            }
                            default -> {
                                radius = new Vec3i(tool.radius().get().getZ(), tool.radius().get().getY(), tool.radius().get().getX());
                                startPos = pos.offset(-radius.getX() * 2, Math.max(-radius.getY(), -1), -radius.getZ());
                            }
                        }
                        for (int x = 0; x < radius.getX() * 2 + 1; x++) {
                            for (int y = 0; y < radius.getY() * 2 + 1; y++) {
                                for (int z = 0; z < radius.getZ() * 2 + 1; z++) {
                                    BlockPos target = startPos.offset(x, y, z);
                                    if (!target.equals(pos)) {
                                        BlockState block = level.getBlockState(target);
                                        if (block.canHarvestBlock(level, target, player)) {
                                            boolean removed = state.onDestroyedByPlayer(level, target, player, true, level.getFluidState(target));
                                            if (removed) {
                                                state.getBlock().destroy(level, target, state);
                                                block.getBlock().playerDestroy(level, player, target, block, level.getBlockEntity(target), stack);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    public void onAttackTick(Level level, Player player, ItemStack stack, int used) {
        ItemStack boer = Utils.getBoerContentsOrEmpty(stack).items;
        if (!boer.isEmpty()) {
            List<LivingEntity> targetEntities = getTargetEntity(player, level);

            if (!targetEntities.isEmpty()) {
                for (LivingEntity target : targetEntities) {
                    if (level.isClientSide) {
                        Vec3 hitPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
                        Vec3 playerEye = player.getEyePosition();

                        if (target.getType() == EntityType.IRON_GOLEM) {
                            spawnEntitySparks(level, hitPos, playerEye, target);
                        }
                    } else {
                        if (player.tickCount % 10 == 0) {
                            target.hurt(level.damageSources().playerAttack(player), 2.0F);
                            boer.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                            Utils.setBoerContents(stack, new BoerContents(boer));

                            level.playSound(null, target.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.3F, 1.8F);
                        }
                    }
                }
                return;
            }

            if (level.isClientSide) {
                BlockHitResult result = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
                if (result.getType() == HitResult.Type.BLOCK) {
                    if (BoersClientConfig.CONFIG.BREAKING_SOUNDS.get()) {
                        if (ModClientEvents.soundInstance == null || !Minecraft.getInstance().getSoundManager().isActive(ModClientEvents.soundInstance)) {
                            if (ModClientEvents.idleSoundInstance != null) {
                                ModClientEvents.idleSoundInstance.remove();
                                ModClientEvents.idleSoundInstance2.remove();
                            }
                            ModClientEvents.soundInstance = new BoerSoundInstance(ModSounds.STONE.get(), SoundSource.PLAYERS, 0.25f, 1f, player, player.getRandom().nextLong());
                            ModClientEvents.soundInstance2 = new BoerSoundInstance(ModSounds.STONE.get(), SoundSource.PLAYERS, 0.25f, 1f, player, player.getRandom().nextLong());
                            Minecraft.getInstance().getSoundManager().play(ModClientEvents.soundInstance);
                            Minecraft.getInstance().getSoundManager().playDelayed(ModClientEvents.soundInstance2, 4);
                        }
                    }
                    Minecraft.getInstance().particleEngine.addBlockHitEffects(result.getBlockPos(), result);
                    spawnSparks(level, player, result);
                } else {
                    if (BoersClientConfig.CONFIG.BREAKING_SOUNDS.get()) {
                        if (ModClientEvents.idleSoundInstance == null || !Minecraft.getInstance().getSoundManager().isActive(ModClientEvents.idleSoundInstance)) {
                            if (ModClientEvents.soundInstance != null) {
                                ModClientEvents.soundInstance.remove();
                                ModClientEvents.soundInstance2.remove();
                            }
                            ModClientEvents.idleSoundInstance = new BoerSoundInstance(ModSounds.AIR.get(), SoundSource.PLAYERS, 0.25f, 1f, player, player.getRandom().nextLong());
                            ModClientEvents.idleSoundInstance2 = new BoerSoundInstance(ModSounds.AIR.get(), SoundSource.PLAYERS, 0.25f, 1f, player, player.getRandom().nextLong());
                            Minecraft.getInstance().getSoundManager().play(ModClientEvents.idleSoundInstance);
                            Minecraft.getInstance().getSoundManager().playDelayed(ModClientEvents.idleSoundInstance2, 5);
                        }
                    }
                }
            } else {
                Utils.increaseUseFor(stack);
            }
        }
    }

    private List<LivingEntity> getTargetEntity(Player player, Level level) {
        double reachDistance = 1.5f;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 reachVec = eyePos.add(lookVec.scale(reachDistance));
        return level.getEntitiesOfClass(LivingEntity.class, new AABB(reachVec.x, reachVec.y, reachVec.z, reachVec.x, reachVec.y, reachVec.z).inflate(1),
                        entity -> !entity.isSpectator() && entity != player);
    }

    private void spawnEntitySparks(Level level, Vec3 hitPos, Vec3 playerEye, LivingEntity target) {
        int sparkCount = 5 + level.random.nextInt(6);

        for (int i = 0; i < sparkCount; i++) {
            double spreadX = (level.random.nextDouble() - 0.5) * target.getBbWidth() * 0.8;
            double spreadY = (level.random.nextDouble() - 0.5) * target.getBbHeight() * 0.5;
            double spreadZ = (level.random.nextDouble() - 0.5) * target.getBbWidth() * 0.8;

            Vec3 sparkPos = hitPos.add(spreadX, spreadY, spreadZ);

            Vec3 velocity = SparkParticle.generateConeVelocity(
                    sparkPos, playerEye, 0.5F
            );

            level.addParticle(
                    ModParticles.SPARK_PARTICLE.get(),
                    sparkPos.x, sparkPos.y, sparkPos.z,
                    velocity.x, velocity.y, velocity.z
            );
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return false;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        BoerHead head = Utils.getBoer(Utils.getBoerContentsOrEmpty(stack).items);
        if (head != null) {
            return head.isCorrectForDrops(state);
        }
        return super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (stack.getCount() != 1 || action != ClickAction.SECONDARY) {
            return false;
        } else {
            BoerContents bundlecontents = Utils.getBoerContents(stack);
            if (bundlecontents == null) {
                return false;
            } else {
                ItemStack itemstack = slot.getItem();

                BoerContents.Mutable bundlecontents$mutable = new BoerContents.Mutable(bundlecontents);
                if (itemstack.isEmpty()) {
                    this.playRemoveOneSound(player);
                    ItemStack itemstack1 = bundlecontents$mutable.removeOne();
                    if (itemstack1 != null) {
                        ItemStack itemstack2 = slot.safeInsert(itemstack1);
                        bundlecontents$mutable.tryInsert(itemstack2);
                    }
                } else if (itemstack.canFitInsideContainerItems()) { // Neo: stack-aware placeability check
                    if (!itemstack.is(ModItems.BOER_HEAD)) {
                        return false;
                    }
                    int i = bundlecontents$mutable.tryTransfer(slot, player);
                    if (i > 0) {
                        this.playInsertSound(player);
                    }
                }

                Utils.setBoerContents(stack, bundlecontents$mutable.toImmutable());
                return true;
            }
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access
    ) {
        if (stack.getCount() != 1) return false;
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            BoerContents bundlecontents = Utils.getBoerContents(stack);
            if (bundlecontents == null) {
                return false;
            } else {
                BoerContents.Mutable bundlecontents$mutable = new BoerContents.Mutable(bundlecontents);
                if (other.isEmpty()) {
                    ItemStack itemstack = bundlecontents$mutable.removeOne();
                    if (itemstack != null) {
                        this.playRemoveOneSound(player);
                        access.set(itemstack);
                    }
                } else {
                    if (!other.is(ModItems.BOER_HEAD)) {
                        return false;
                    }
                    ItemStack itemStack = bundlecontents$mutable.removeOne();

                    int i = bundlecontents$mutable.tryInsert(other);
                    if (i > 0) {
                        this.playInsertSound(player);
                    }
                    if (itemStack != null) {
                        access.set(itemStack);
                    }
                }

                Utils.setBoerContents(stack,bundlecontents$mutable.toImmutable());
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        ItemStack boer = Utils.getBoerContentsOrEmpty(stack).items;
        return !boer.isEmpty() && boer.isDamaged();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Utils.getBoerContentsOrEmpty(stack).items.getBarWidth();
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(Utils.getBoerContents(stack))
                : Optional.empty();
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        BoerContents bundlecontents = Utils.getBoerContents(itemEntity.getItem());
        if (bundlecontents != null) {
            Utils.setBoerContents(itemEntity.getItem(), BoerContents.EMPTY);
            ItemUtils.onContainerDestroyed(itemEntity, List.of(bundlecontents.itemsCopy()));
        }
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(ModSounds.HEAD_UNEQUIP.get(), 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(ModSounds.HEAD_EQUIP.get(), 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }


    private void spawnSparks(Level level, Player player, BlockHitResult hitResult) {
        if (level.getBlockState(hitResult.getBlockPos()).getDestroySpeed(level, hitResult.getBlockPos()) < 1.1) return;

        Vec3 hitPos = hitResult.getLocation();
        Vec3 playerEye = player.getEyePosition();
        Direction blockFace = hitResult.getDirection();

        Vec3 offset = Vec3.atLowerCornerOf(blockFace.getNormal()).scale(0.05);
        Vec3 spawnPos = hitPos.add(offset);

        int sparkCount = BoersClientConfig.CONFIG.PARTICLE_COUNT.get() + level.random.nextInt(BoersClientConfig.CONFIG.PARTICLE_COUNT.get());

        for (int i = 0; i < sparkCount; i++) {
            double spreadX = (level.random.nextDouble() - 0.5) * 0.15;
            double spreadY = (level.random.nextDouble() - 0.5) * 0.15;
            double spreadZ = (level.random.nextDouble() - 0.5) * 0.15;

            Vec3 sparkPos = spawnPos.add(spreadX, spreadY, spreadZ);

            Vec3 velocity = SparkParticle.generateConeVelocity(
                    hitPos, playerEye, 0.4F
            );

            level.addParticle(
                    ModParticles.SPARK_PARTICLE.get(),
                    sparkPos.x, sparkPos.y, sparkPos.z,
                    velocity.x, velocity.y, velocity.z
            );
        }
    }
}
