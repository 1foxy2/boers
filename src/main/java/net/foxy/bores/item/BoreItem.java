package net.foxy.bores.item;

import net.foxy.bores.base.ModDataComponents;
import net.foxy.bores.base.ModItems;
import net.foxy.bores.base.ModParticles;
import net.foxy.bores.base.ModSounds;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.event.ModClientEvents;
import net.foxy.bores.particle.spark.SparkParticle;
import net.foxy.bores.util.Utils;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class BoreItem extends Item {
    public BoreItem() {
        super(new Properties().stacksTo(1)
                .component(ModDataComponents.BORE_CONTENTS, BoreContents.EMPTY)
                .component(DataComponents.BASE_COLOR, DyeColor.BLUE)
                .component(DataComponents.DAMAGE, 0)
                .component(DataComponents.MAX_DAMAGE, 0)
                .rarity(Rarity.EPIC)
        );
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        ItemStack boreItem = Utils.getBoreContents(stack).getItemUnsafe();
        return !boreItem.isEmpty() && boreItem.isDamaged();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        ItemStack boreItem = Utils.getBoreContents(stack).getItemUnsafe();
        return !boreItem.isEmpty() && boreItem.isDamageableItem();
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        ItemStack boreItem = Utils.getBoreContents(stack).itemsCopy();
        if (!boreItem.isEmpty()) {
            boreItem.setDamageValue(damage);
            Utils.setBoreContents(stack, new BoreContents(boreItem));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            if (isSelected) {
                if (player.getOffhandItem().isEmpty()) {
                    entity.setYBodyRot(entity.getYHeadRot() + (player.getMainArm() == HumanoidArm.LEFT ? -37 : 37));
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
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Utils.getBoreContentsOrEmpty(stack).items.getMaxDamage();
    }

    @Override
    public int getDamage(ItemStack stack) {
        return Utils.getBoreContentsOrEmpty(stack).items.getDamageValue();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(newStack.getItem());
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        BoreHead tool = Utils.getBore(Utils.getBoreContentsOrEmpty(stack).items);
        return tool != null ? tool.getMiningSpeed(stack, state) : 1.0F;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !oldStack.is(newStack.getItem());
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        BoreContents boreContents = Utils.getBoreContents(stack);
        if (boreContents == null) {
            return false;
        } else {
            if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
                ItemStack bore = boreContents.getItemUnsafe();
                BoreHead tool = Utils.getBore(Utils.getBoreContentsOrEmpty(stack).items);
                int damage = tool != null ? tool.getDamage(state) : 1;
                bore.hurtAndBreak(damage, miningEntity, EquipmentSlot.MAINHAND);
                Utils.setBoreContents(stack, new BoreContents(bore));
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
    public int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
        ItemStack contents = Utils.getBoreContents(stack).items;
        if (!contents.isEmpty()) {
            return contents.getEnchantmentLevel(enchantment);
        }

        return super.getEnchantmentLevel(stack, enchantment);
    }

    @Override
    public ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
        ItemStack contents = Utils.getBoreContents(stack).items;
        if (!contents.isEmpty()) {
            return contents.getAllEnchantments(lookup);
        }

        return super.getAllEnchantments(stack, lookup);
    }

    public void onAttackTick(Level level, Player player, ItemStack stack, int used) {
        ItemStack bore = Utils.getBoreContentsOrEmpty(stack).items;
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
                            bore.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                            Utils.setBoreContents(stack, new BoreContents(bore));

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
        BoreHead head = Utils.getBore(Utils.getBoreContentsOrEmpty(stack).items);
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
            BoreContents bundlecontents = Utils.getBoreContents(stack);
            if (bundlecontents == null) {
                return false;
            } else {
                ItemStack itemstack = slot.getItem();

                BoreContents.Mutable bundlecontents$mutable = new BoreContents.Mutable(bundlecontents);
                if (itemstack.isEmpty()) {
                    this.playRemoveOneSound(player);
                    ItemStack itemstack1 = bundlecontents$mutable.removeOne();
                    if (itemstack1 != null) {
                        ItemStack itemstack2 = slot.safeInsert(itemstack1);
                        bundlecontents$mutable.tryInsert(itemstack2);
                    }
                } else if (itemstack.getItem().canFitInsideContainerItems()) { // Neo: stack-aware placeability check
                    if (!itemstack.is(ModItems.BORE_HEAD)) {
                        return false;
                    }
                    int i = bundlecontents$mutable.tryTransfer(slot, player);
                    if (i > 0) {
                        this.playInsertSound(player);
                    }
                }

                Utils.setBoreContents(stack, bundlecontents$mutable.toImmutable());
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
            BoreContents bundlecontents = Utils.getBoreContents(stack);
            if (bundlecontents == null) {
                return false;
            } else {
                BoreContents.Mutable bundlecontents$mutable = new BoreContents.Mutable(bundlecontents);
                if (other.isEmpty()) {
                    ItemStack itemstack = bundlecontents$mutable.removeOne();
                    if (itemstack != null) {
                        this.playRemoveOneSound(player);
                        access.set(itemstack);
                    }
                } else {
                    if (!other.is(ModItems.BORE_HEAD)) {
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

                Utils.setBoreContents(stack,bundlecontents$mutable.toImmutable());
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        ItemStack bore = Utils.getBoreContentsOrEmpty(stack).items;
        return !bore.isEmpty() && bore.isDamaged();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Utils.getBoreContentsOrEmpty(stack).items.getBarWidth();
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return !stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(Utils.getBoreContents(stack))
                : Optional.empty();
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        BoreContents bundlecontents = Utils.getBoreContents(itemEntity.getItem());
        if (bundlecontents != null) {
            Utils.setBoreContents(itemEntity.getItem(), BoreContents.EMPTY);
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
}
