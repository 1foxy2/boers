package net.foxy.drills.item;

import net.foxy.drills.base.ModDataComponents;
import net.foxy.drills.base.ModItems;
import net.foxy.drills.base.ModParticles;
import net.foxy.drills.base.ModSounds;
import net.foxy.drills.client.DrillSoundInstance;
import net.foxy.drills.client.DrillsClientConfig;
import net.foxy.drills.data.DrillHead;
import net.foxy.drills.event.ModClientEvents;
import net.foxy.drills.particle.spark.SparkParticle;
import net.foxy.drills.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
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

public class DrillBaseItem extends Item {
    public DrillBaseItem() {
        super(new Properties().stacksTo(1)
                .component(ModDataComponents.DRILL_CONTENTS, DrillContents.EMPTY).component(DataComponents.BASE_COLOR, DyeColor.BLUE)
        );
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        ItemStack drillItem = stack.get(ModDataComponents.DRILL_CONTENTS).getItemUnsafe();
        return !drillItem.isEmpty() && drillItem.isDamageableItem();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected) {
            entity.setYBodyRot(entity.getYHeadRot() + 37);
            if (entity instanceof Player player && stack.getOrDefault(ModDataComponents.IS_USED, false)) {
                player.attackStrengthTicker = 10;
                player.swinging = false;
                player.attackAnim = 0;
                player.swingTime = 0;
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.DRILL_CONTENTS.get(), DrillContents.EMPTY).items.getMaxDamage();
    }

    @Override
    public int getDamage(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.DRILL_CONTENTS.get(), DrillContents.EMPTY).items.getDamageValue();
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
        DrillHead tool = Utils.getDrill(stack.getOrDefault(ModDataComponents.DRILL_CONTENTS, DrillContents.EMPTY).items);
        return tool != null ? tool.getMiningSpeed(state) : 1.0F;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        DrillContents drillContents = stack.get(ModDataComponents.DRILL_CONTENTS);
        if (drillContents == null) {
            return false;
        } else {
            if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
                ItemStack drill = drillContents.getItemUnsafe();
                drill.hurtAndBreak(1, miningEntity, EquipmentSlot.MAINHAND);
                stack.set(ModDataComponents.DRILL_CONTENTS.get(), new DrillContents(drill));
            }

            return true;
        }
    }

    public void onAttackTick(Level level, Player player, ItemStack stack, int used) {
        ItemStack drill = stack.getOrDefault(ModDataComponents.DRILL_CONTENTS, DrillContents.EMPTY).items;
        if (!drill.isEmpty()) {
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
                            drill.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                            stack.set(ModDataComponents.DRILL_CONTENTS, new DrillContents(drill));

                            level.playSound(null, target.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.3F, 1.8F);
                        }
                    }
                }
                return;
            }

            if (level.isClientSide) {
                BlockHitResult result = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
                if (result.getType() == HitResult.Type.BLOCK) {
                    if (DrillsClientConfig.CONFIG.BREAKING_SOUNDS.get()) {
                        if (ModClientEvents.soundInstance == null || !Minecraft.getInstance().getSoundManager().isActive(ModClientEvents.soundInstance)) {
                            if (ModClientEvents.idleSoundInstance != null)
                                ModClientEvents.idleSoundInstance.remove();
                            ModClientEvents.soundInstance = new DrillSoundInstance(ModSounds.STONE.get(), SoundSource.PLAYERS, 0.25f, 1f, player, player.getRandom().nextLong());
                            Minecraft.getInstance().getSoundManager().play(ModClientEvents.soundInstance);
                        }
                    }
                    Minecraft.getInstance().particleEngine.addBlockHitEffects(result.getBlockPos(), result);
                    spawnSparks(level, player, result);
                } else {
                    if (DrillsClientConfig.CONFIG.BREAKING_SOUNDS.get()) {
                        if (ModClientEvents.idleSoundInstance == null || !Minecraft.getInstance().getSoundManager().isActive(ModClientEvents.idleSoundInstance)) {
                            if (ModClientEvents.soundInstance != null)
                                ModClientEvents.soundInstance.remove();
                            ModClientEvents.idleSoundInstance = new DrillSoundInstance(ModSounds.AIR.get(), SoundSource.PLAYERS, 0.25f, 1f, player, player.getRandom().nextLong());
                            Minecraft.getInstance().getSoundManager().play(ModClientEvents.idleSoundInstance);
                        }
                    }
                }
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
        DrillHead head = Utils.getDrill(stack.getOrDefault(ModDataComponents.DRILL_CONTENTS, DrillContents.EMPTY).items);
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
            DrillContents bundlecontents = stack.get(ModDataComponents.DRILL_CONTENTS);
            if (bundlecontents == null) {
                return false;
            } else {
                ItemStack itemstack = slot.getItem();

                DrillContents.Mutable bundlecontents$mutable = new DrillContents.Mutable(bundlecontents);
                if (itemstack.isEmpty()) {
                    this.playRemoveOneSound(player);
                    ItemStack itemstack1 = bundlecontents$mutable.removeOne();
                    if (itemstack1 != null) {
                        ItemStack itemstack2 = slot.safeInsert(itemstack1);
                        bundlecontents$mutable.tryInsert(itemstack2);
                    }
                } else if (itemstack.canFitInsideContainerItems()) { // Neo: stack-aware placeability check
                    if (!itemstack.is(ModItems.DRILL_HEAD)) {
                        return false;
                    }
                    int i = bundlecontents$mutable.tryTransfer(slot, player);
                    if (i > 0) {
                        this.playInsertSound(player);
                    }
                }

                stack.set(ModDataComponents.DRILL_CONTENTS, bundlecontents$mutable.toImmutable());
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
            DrillContents bundlecontents = stack.get(ModDataComponents.DRILL_CONTENTS);
            if (bundlecontents == null) {
                return false;
            } else {
                DrillContents.Mutable bundlecontents$mutable = new DrillContents.Mutable(bundlecontents);
                if (other.isEmpty()) {
                    ItemStack itemstack = bundlecontents$mutable.removeOne();
                    if (itemstack != null) {
                        this.playRemoveOneSound(player);
                        access.set(itemstack);
                    }
                } else {
                    if (!other.is(ModItems.DRILL_HEAD)) {
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

                stack.set(ModDataComponents.DRILL_CONTENTS, bundlecontents$mutable.toImmutable());
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        ItemStack drill = stack.getOrDefault(ModDataComponents.DRILL_CONTENTS.get(), DrillContents.EMPTY).items;
        return !drill.isEmpty() && drill.isDamaged();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.DRILL_CONTENTS.get(), DrillContents.EMPTY).items.getBarWidth();
    }

    private static boolean dropContents(ItemStack stack, Player player) {
        DrillContents bundlecontents = stack.get(ModDataComponents.DRILL_CONTENTS);
        if (bundlecontents != null && !bundlecontents.isEmpty()) {
            stack.set(ModDataComponents.DRILL_CONTENTS, DrillContents.EMPTY);
            if (player instanceof ServerPlayer) {
                player.drop(bundlecontents.itemsCopy(), true);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(stack.get(ModDataComponents.DRILL_CONTENTS))
                : Optional.empty();
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        DrillContents bundlecontents = itemEntity.getItem().get(ModDataComponents.DRILL_CONTENTS);
        if (bundlecontents != null) {
            itemEntity.getItem().set(ModDataComponents.DRILL_CONTENTS, DrillContents.EMPTY);
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
