package net.foxy.drills.item;

import com.mojang.logging.LogUtils;
import net.foxy.drills.base.ModDataComponents;
import net.foxy.drills.base.ModItems;
import net.foxy.drills.base.ModParticles;
import net.foxy.drills.base.ModSounds;
import net.foxy.drills.client.DrillSoundInstance;
import net.foxy.drills.data.DrillHead;
import net.foxy.drills.particle.spark.SparkParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Locale;
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

            if (entity instanceof LivingEntity livingEntity && livingEntity.getUseItem() == stack) {
                return;
            }

            int progress = stack.getOrDefault(ModDataComponents.USED, 1);
            if (progress < 0) {
                if (progress == -1) {
                    stack.remove(ModDataComponents.USED);
                } else {
                    stack.set(ModDataComponents.USED, progress + 1);
                }
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
        stack.set(ModDataComponents.USED, Math.max(-10, -stack.getOrDefault(ModDataComponents.USED, 0)));
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.gameMode.handleBlockBreakAction(serverPlayer.gameMode.destroyPos,
                    ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, Direction.UP, entity.level().getMaxBuildHeight(), 0);
        }
        stack.remove(ModDataComponents.BREAKING_POS);
        stack.remove(ModDataComponents.START_TICK);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        ItemStack stack = player.getItemInHand(usedHand);
        stack.set(ModDataComponents.USED, -stack.getOrDefault(ModDataComponents.USED, 0));

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(newStack.getItem());
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        DrillHead tool = stack.getOrDefault(ModDataComponents.DRILL_CONTENTS, DrillContents.EMPTY).items.get(ModDataComponents.DRILL);
        return tool != null ? tool.getMiningSpeed(state) : 1.0F;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        int used = stack.getOrDefault(ModDataComponents.USED, 0) + 1;
        stack.set(ModDataComponents.USED, used);

        if (used > 9 && livingEntity instanceof Player player) {
            ItemStack drill = stack.getOrDefault(ModDataComponents.DRILL_CONTENTS, DrillContents.EMPTY).items;
            if (!drill.isEmpty()) {

                Entity targetEntity = getTargetEntity(player, level);

                if (targetEntity instanceof LivingEntity target && target.getType() == EntityType.IRON_GOLEM) {
                    if (level.isClientSide) {
                        Vec3 hitPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
                        Vec3 playerEye = player.getEyePosition();
                        spawnEntitySparks(level, hitPos, playerEye, target);
                    } else {
                        if (used % 10 == 0) {
                            target.hurt(level.damageSources().playerAttack(player), 1.0F);
                            drill.hurtAndBreak(1, livingEntity, livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                            stack.set(ModDataComponents.DRILL_CONTENTS, new DrillContents(drill));

                            level.playSound(null, target.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.3F, 1.8F);
                        }
                    }
                    return;
                }

                BlockHitResult result = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
                if (result.getType() == HitResult.Type.BLOCK) {
                    if (level.isClientSide) {
                        if (player.tickCount % 30 == 0 || used == 10) {
                            Minecraft.getInstance().getSoundManager().play(new DrillSoundInstance(ModSounds.STONE.get(), SoundSource.PLAYERS, 1f, 1f, livingEntity, player.getRandom().nextLong()));
                        }
                        Minecraft.getInstance().particleEngine.addBlockHitEffects(result.getBlockPos(), result);
                        spawnSparks(level, player, result);
                    } else if (player instanceof ServerPlayer serverPlayer) {
                        BlockPos pos = stack.get(ModDataComponents.BREAKING_POS);
                        if (pos == null || !pos.equals(result.getBlockPos())) {
                            pos = result.getBlockPos();
                            stack.set(ModDataComponents.BREAKING_POS, pos);
                            stack.set(ModDataComponents.START_TICK, remainingUseDuration);
                        }

                        int startTick = stack.get(ModDataComponents.START_TICK);
                        BlockState state = level.getBlockState(pos);

                        int i = startTick - remainingUseDuration;
                        float progress = state.getDestroyProgress(player, level, pos) * (float) (i + 1);
                        level.destroyBlockProgress(-1, pos, (int) (progress * 10));
                        if (progress >= 1) {
                            level.levelEvent(2001, pos, Block.getId(state));
                            serverPlayer.gameMode.destroyBlock(pos);
                            drill.hurtAndBreak(1, livingEntity, livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                            stack.set(ModDataComponents.DRILL_CONTENTS.get(), new DrillContents(drill));
                        }
                    }
                } else if (level.isClientSide) {
                    if (player.tickCount % 30 == 0 || used == 10) {
                        Minecraft.getInstance().getSoundManager().play(new DrillSoundInstance(ModSounds.AIR.get(), SoundSource.PLAYERS, 1f, 1f, livingEntity, player.getRandom().nextLong()));
                    }
                }
            }
        }

        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    private Entity getTargetEntity(Player player, Level level) {
        double reachDistance = 5.0;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 reachVec = eyePos.add(lookVec.scale(reachDistance));

        return level.getEntities(player, player.getBoundingBox().expandTowards(lookVec.scale(reachDistance)).inflate(1.0),
                        entity -> entity instanceof LivingEntity && !entity.isSpectator())
                .stream()
                .filter(entity -> {
                    Vec3 entityVec = entity.getBoundingBox().getCenter();
                    double distance = eyePos.distanceTo(entityVec);
                    return distance <= reachDistance;
                })
                .min((e1, e2) -> {
                    double d1 = eyePos.distanceToSqr(e1.position());
                    double d2 = eyePos.distanceToSqr(e2.position());
                    return Double.compare(d1, d2);
                })
                .orElse(null);
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
        DrillHead head = stack.getOrDefault(ModDataComponents.DRILL_CONTENTS, DrillContents.EMPTY).items.get(ModDataComponents.DRILL);
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
        if (!isHardMaterial(level.getBlockState(hitResult.getBlockPos()))) return;

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

    private boolean isHardMaterial(BlockState state) {
        Block block = state.getBlock();

        if (state.is(Tags.Blocks.STONES)) return true;
        if (state.is(Tags.Blocks.ORES)) return true;
        if (state.is(BlockTags.NEEDS_IRON_TOOL)) return true;
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) return true;

        if (state.is(Tags.Blocks.STORAGE_BLOCKS_IRON)) return true;
        if (state.is(Tags.Blocks.STORAGE_BLOCKS_GOLD)) return true;
        if (state.is(Tags.Blocks.STORAGE_BLOCKS_COPPER)) return true;
        if (state.is(Tags.Blocks.STORAGE_BLOCKS_DIAMOND)) return true;
        if (state.is(Tags.Blocks.STORAGE_BLOCKS_EMERALD)) return true;
        if (state.is(Tags.Blocks.STORAGE_BLOCKS_NETHERITE)) return true;

        if (block == Blocks.IRON_BLOCK) return true;
        if (block == Blocks.GOLD_BLOCK) return true;
        if (block == Blocks.DIAMOND_BLOCK) return true;
        if (block == Blocks.EMERALD_BLOCK) return true;
        if (block == Blocks.NETHERITE_BLOCK) return true;
        if (block == Blocks.COPPER_BLOCK) return true;
        if (block == Blocks.EXPOSED_COPPER) return true;
        if (block == Blocks.WEATHERED_COPPER) return true;
        if (block == Blocks.OXIDIZED_COPPER) return true;

        if (block == Blocks.ANVIL) return true;
        if (block == Blocks.CHIPPED_ANVIL) return true;
        if (block == Blocks.DAMAGED_ANVIL) return true;
        if (block == Blocks.IRON_BARS) return true;
        if (block == Blocks.IRON_DOOR) return true;
        if (block == Blocks.IRON_TRAPDOOR) return true;
        if (block == Blocks.HOPPER) return true;
        if (block == Blocks.CAULDRON) return true;
        if (block == Blocks.LAVA_CAULDRON) return true;
        if (block == Blocks.WATER_CAULDRON) return true;
        if (block == Blocks.POWDER_SNOW_CAULDRON) return true;
        if (block == Blocks.CHAIN) return true;
        if (block == Blocks.LANTERN) return true;
        if (block == Blocks.SOUL_LANTERN) return true;

        if (block == Blocks.RAIL) return true;
        if (block == Blocks.POWERED_RAIL) return true;
        if (block == Blocks.DETECTOR_RAIL) return true;
        if (block == Blocks.ACTIVATOR_RAIL) return true;

        if (block == Blocks.OBSIDIAN) return true;
        if (block == Blocks.CRYING_OBSIDIAN) return true;
        if (block == Blocks.ANCIENT_DEBRIS) return true;
        if (block == Blocks.NETHERITE_BLOCK) return true;

        if (block == Blocks.LODESTONE) return true;
        if (block == Blocks.RESPAWN_ANCHOR) return true;

        if (block == Blocks.BLAST_FURNACE) return true;
        if (block == Blocks.FURNACE) return true;
        if (block == Blocks.SMOKER) return true;

        if (block == Blocks.BELL) return true;

        String blockId = BuiltInRegistries.BLOCK.getKey(block).getPath();
        if (blockId.contains("iron")) return true;
        if (blockId.contains("steel")) return true;
        if (blockId.contains("metal")) return true;
        if (blockId.contains("copper")) return true;

        return false;
    }
}
