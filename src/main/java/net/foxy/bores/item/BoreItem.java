package net.foxy.bores.item;

import net.foxy.bores.base.ModEnums;
import net.foxy.bores.base.ModItems;
import net.foxy.bores.base.ModParticles;
import net.foxy.bores.base.ModSounds;
import net.foxy.bores.client.BoreRenderer;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.event.ModClientEvents;
import net.foxy.bores.particle.spark.SparkParticle;
import net.foxy.bores.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class BoreItem extends Item {
    public BoreItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public static BoreRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new BoreRenderer(
                            Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                            Minecraft.getInstance().getEntityModels());
                }

                return renderer;
            }

            @Override
            public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return ModEnums.BORE_STANDING_POS;
            }
        });
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        ItemStack boreItem = Utils.getBoreContents(stack);
        return !boreItem.isEmpty() && boreItem.isDamaged();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        ItemStack boreItem = Utils.getBoreContents(stack);
        return !boreItem.isEmpty() && boreItem.isDamageableItem();
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        ItemStack boreItem = Utils.getBoreContents(stack);
        if (!boreItem.isEmpty()) {
            boreItem.setDamageValue(damage);
            Utils.setBoreContents(stack, boreItem);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            if (isSelected) {
                if (player.getOffhandItem().isEmpty()) {
                    entity.setYBodyRot(entity.getYHeadRot() + (player.getMainArm() == HumanoidArm.LEFT ? -37 : 37));
                    Utils.setDouble(stack, true);
                } else {
                    Utils.setDouble(stack, false);
                }
                if (Utils.isUsed(stack)) {
                    player.swinging = false;
                    player.attackAnim = 0;
                    player.swingTime = 0;
                } else {
                    Utils.decreaseUseFor(stack);
                }
            } else {
                Utils.setUsed(stack, false);
                Utils.decreaseUseFor(stack);
                if (slotId == Inventory.SLOT_OFFHAND) {
                    Utils.setDouble(stack, player.getMainHandItem().isEmpty());
                }
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Utils.getBoreContentsOrEmpty(stack).getMaxDamage();
    }

    @Override
    public int getDamage(ItemStack stack) {
        return Utils.getBoreContentsOrEmpty(stack).getDamageValue();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(newStack.getItem());
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        BoreHead tool = Utils.getBore(Utils.getBoreContentsOrEmpty(stack));
        return tool != null ? tool.getMiningSpeed(stack, state) : 1.0F;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !oldStack.is(newStack.getItem());
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        ItemStack bore = Utils.getBoreContents(stack);
        if (bore.isEmpty()) {
            return false;
        } else {
            if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
                BoreHead tool = Utils.getBore(Utils.getBoreContentsOrEmpty(stack));
                int damage = tool != null ? tool.getDamage(state) : 1;
                bore.hurtAndBreak(damage, miningEntity, player -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                Utils.setBoreContents(stack, bore);
                if (tool != null && tool.radius().isPresent() && miningEntity instanceof ServerPlayer player) {
                    Utils.forEachBlock(level, player, pos, tool.radius().get(), (target, block) -> {
                        boolean removed = block.onDestroyedByPlayer(level, target, player, true, level.getFluidState(target));
                        if (removed) {
                            block.getBlock().destroy(level, target, block);
                            block.getBlock().playerDestroy(level, player, target, block, level.getBlockEntity(target), stack);
                        }
                    });
                }
            }

            return true;
        }
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        ItemStack contents = Utils.getBoreContents(stack);
        if (!contents.isEmpty()) {
            return contents.getEnchantmentLevel(enchantment);
        }

        return super.getEnchantmentLevel(stack, enchantment);
    }


    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        ItemStack contents = Utils.getBoreContents(stack);
        if (!contents.isEmpty()) {
            return contents.getAllEnchantments();
        }

        return super.getAllEnchantments(stack);
    }

    public void onAttackTick(Level level, Player player, ItemStack stack, int used) {
        ItemStack bore = Utils.getBoreContentsOrEmpty(stack);
        if (!bore.isEmpty()) {
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
                            bore.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                            Utils.setBoreContents(stack, bore);

                            level.playSound(null, target.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.3F, 1.8F);
                        }
                    }
                }
                return;
            }

            if (level.isClientSide) {
                ModClientEvents.handleTick(level, player, stack);
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
        BoreHead head = Utils.getBore(Utils.getBoreContentsOrEmpty(stack));
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
            ItemStack bundlecontents = Utils.getBoreContents(stack);

            ItemStack itemstack = slot.getItem();

            if (itemstack.isEmpty()) {
                this.playRemoveOneSound(player);
                ItemStack itemstack1 = bundlecontents;
                if (itemstack1 != null) {
                    bundlecontents = slot.safeInsert(itemstack1);
                }
            } else if (itemstack.getItem().canFitInsideContainerItems()) { // Neo: stack-aware placeability check
                if (!itemstack.is(ModItems.BORE_HEAD.get())) {
                    return false;
                }
                if (bundlecontents.isEmpty()) {
                    bundlecontents = slot.safeTake(1, 1, player);
                    this.playInsertSound(player);
                }
            }

            Utils.setBoreContents(stack, bundlecontents);
            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access
    ) {
        if (stack.getCount() != 1) return false;
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            ItemStack bundlecontents = Utils.getBoreContents(stack);
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
                    if (!other.is(ModItems.BORE_HEAD.get())) {
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

                Utils.setBoreContents(stack, bundlecontents);
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        ItemStack bore = Utils.getBoreContentsOrEmpty(stack);
        return !bore.isEmpty() && bore.isDamaged();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Utils.getBoreContentsOrEmpty(stack).getBarWidth();
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new BoreTooltip(Utils.getBoreContents(stack)));
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        ItemStack bundlecontents = Utils.getBoreContents(itemEntity.getItem());
        if (bundlecontents != null) {
            Utils.setBoreContents(itemEntity.getItem(), ItemStack.EMPTY);
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
}
