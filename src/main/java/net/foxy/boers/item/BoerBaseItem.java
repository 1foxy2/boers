package net.foxy.boers.item;

import net.foxy.boers.base.*;
import net.foxy.boers.client.BoerBaseRenderer;
import net.foxy.boers.client.BoerSoundInstance;
import net.foxy.boers.client.BoersClientConfig;
import net.foxy.boers.data.BoerHead;
import net.foxy.boers.event.ModClientEvents;
import net.foxy.boers.particle.spark.SparkParticle;
import net.foxy.boers.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BoerBaseItem extends Item {
    public BoerBaseItem() {
        super(new Properties().stacksTo(1)
                //.component(ModDataComponents.BOER_CONTENTS, BoerContents.EMPTY).component(DataComponents.BASE_COLOR, DyeColor.BLUE)
        );
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public static BoerBaseRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new BoerBaseRenderer(
                            Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                            Minecraft.getInstance().getEntityModels());
                }

                return renderer;
            }

            @Override
            public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return ModEnums.BOER_STANDING_POS;
            }
        });
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        ItemStack boerItem = Utils.getBoerContents(stack);
        return !boerItem.isEmpty() && boerItem.isDamageableItem();
    }

    @Override
    public int getUseDuration(ItemStack stack) {
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
        return Utils.getBoerContentsOrEmpty(stack).getMaxDamage();
    }

    @Override
    public int getDamage(ItemStack stack) {
        return Utils.getBoerContentsOrEmpty(stack).getDamageValue();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(newStack.getItem());
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        BoerHead tool = Utils.getBoer(Utils.getBoerContentsOrEmpty(stack));
        return tool != null ? tool.getMiningSpeed(stack, state) : 1.0F;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !oldStack.is(newStack.getItem());
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        ItemStack boer = Utils.getBoerContents(stack);
        if (boer.isEmpty()) {
            return false;
        } else {
            if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
                BoerHead tool = Utils.getBoer(Utils.getBoerContentsOrEmpty(stack));
                int damage = tool != null ? tool.getDamage(state) : 1;
                boer.hurtAndBreak(damage, miningEntity, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                Utils.setBoerContents(stack, boer);
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
        ItemStack boer = Utils.getBoerContentsOrEmpty(stack);
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
                            boer.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                            Utils.setBoerContents(stack, boer);

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
        BoerHead head = Utils.getBoer(Utils.getBoerContentsOrEmpty(stack));
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
            ItemStack bundlecontents = Utils.getBoerContents(stack);

            ItemStack itemstack = slot.getItem();

            if (itemstack.isEmpty()) {
                this.playRemoveOneSound(player);
                ItemStack itemstack1 = bundlecontents;
                if (itemstack1 != null) {
                    bundlecontents = slot.safeInsert(itemstack1);
                }
            } else if (itemstack.getItem().canFitInsideContainerItems()) { // Neo: stack-aware placeability check
                if (!itemstack.is(ModItems.BOER_HEAD.get())) {
                    return false;
                }
                if (bundlecontents.isEmpty()) {
                    bundlecontents = slot.safeTake(1, 1, player);
                    this.playInsertSound(player);
                }
            }

            Utils.setBoerContents(stack, bundlecontents);
            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access
    ) {
        if (stack.getCount() != 1) return false;
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            ItemStack bundlecontents = Utils.getBoerContents(stack);
            if (bundlecontents == null) {
                return false;
            } else {
                if (other.isEmpty()) {
                    ItemStack itemstack = bundlecontents.split(1);
                    if (itemstack != null) {
                        this.playRemoveOneSound(player);
                        access.set(itemstack);
                    }
                } else {
                    if (!other.is(ModItems.BOER_HEAD.get())) {
                        return false;
                    }
                    ItemStack itemStack = bundlecontents.split(1);

                    if (bundlecontents.isEmpty()) {
                        bundlecontents = other.split(1);
                        this.playInsertSound(player);
                    }
                    if (!itemStack.isEmpty()) {
                        access.set(itemStack);
                    }
                }

                Utils.setBoerContents(stack, bundlecontents);
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        ItemStack boer = Utils.getBoerContentsOrEmpty(stack);
        return !boer.isEmpty() && boer.isDamaged();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Utils.getBoerContentsOrEmpty(stack).getBarWidth();
    }

    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new BoerTooltip(Utils.getBoerContents(stack)));
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        ItemStack bundlecontents = Utils.getBoerContents(itemEntity.getItem());
        if (bundlecontents != null) {
            Utils.setBoerContents(itemEntity.getItem(), ItemStack.EMPTY);
            ItemUtils.onContainerDestroyed(itemEntity, List.of(bundlecontents.copy()).stream());
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

        int sparkCount = 3 + level.random.nextInt(4);

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
