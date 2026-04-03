package net.foxy.bores.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxy.bores.BoresMod;
import net.foxy.bores.base.ModEnums;
import net.foxy.bores.base.ModItems;
import net.foxy.bores.base.ModParticles;
import net.foxy.bores.base.ModSounds;
import net.foxy.bores.client.BoreSoundInstance;
import net.foxy.bores.client.BoresClientConfig;
import net.foxy.bores.client.ClientBoresTooltip;
import net.foxy.bores.client.model.BoreHeadModel;
import net.foxy.bores.client.model.BoreItemSpecialRenderer;
import net.foxy.bores.client.model.BoreModelWrapper;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.item.BoreContents;
import net.foxy.bores.item.BoreItem;
import net.foxy.bores.network.c2s.SetUseBorePacket;
import net.foxy.bores.network.c2s.TickBorePacket;
import net.foxy.bores.particle.spark.SparkParticle;
import net.foxy.bores.particle.spark.SparkParticleProvider;
import net.foxy.bores.util.Utils;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = BoresMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    public static int lastProgress = 0;
    public static int usingProgress = 0;
    public static BoreSoundInstance soundInstance = null;
    public static BoreSoundInstance soundInstance2 = null;
    public static BoreSoundInstance idleSoundInstance = null;
    public static BoreSoundInstance idleSoundInstance2 = null;

    @SubscribeEvent
    public static void renderOutline(ExtractBlockOutlineRenderStateEvent event) {
        if (!(event.getCamera().entity() instanceof Player player)) {
            return;
        }

        ItemStack stack = event.getCamera().entity().getWeaponItem();
        if (stack == null) {
            return;
        }
        BoreContents boreContents = Utils.getBoreContentsOrEmpty(stack);
        Level level = event.getLevel();
        if (boreContents.isEmpty()) {
            return;
        }

        BoreHead tool = Utils.getBore(boreContents.getItem());
        if (tool != null && tool.radius().isPresent()) {
            List<BlockOutlineRenderState> renderStates = new ArrayList<>();
            Utils.forEachBlock(level, player, event.getBlockPos(), tool.radius().get(), (target, state) -> {;
                VoxelShape shape = state.getShape(level, target, event.getCollisionContext());
                BlockOutlineRenderState blockOutlineRenderState;
                if (SharedConstants.DEBUG_SHAPES) {
                    VoxelShape collisionShape = state.getCollisionShape(level, target, event.getCollisionContext());
                    VoxelShape occlusionShape = state.getOcclusionShape();
                    VoxelShape interactionShape = state.getInteractionShape(level, target);
                    blockOutlineRenderState = new BlockOutlineRenderState(
                            target, event.isInTranslucentPass(), event.isHighContrast(), shape,
                            collisionShape, occlusionShape, interactionShape, event.getCustomRenderers()
                    );
                } else {
                    blockOutlineRenderState = new BlockOutlineRenderState(target, event.isInTranslucentPass(), event.isHighContrast(), shape, event.getCustomRenderers());
                }
                renderStates.add(blockOutlineRenderState);
            });
            if (renderStates.isEmpty()) {
                return;
            }
            event.addCustomRenderer((state, bufferSource, poseStack, translucentPass, levelRenderState) -> {
                if (state.isTranslucent() == translucentPass) {
                    Vec3 cameraPos = levelRenderState.cameraRenderState.pos;
                    for (BlockOutlineRenderState renderState : renderStates) {
                        if (renderState.highContrast()) {
                            VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.secondaryBlockOutline());
                            event.getLevelRenderer().renderHitOutline(poseStack, buffer, cameraPos.x, cameraPos.y, cameraPos.z, renderState, -16777216, 7.0F);
                        }

                        VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.lines());
                        int outlineColor = renderState.highContrast() ? -11010079 : ARGB.black(102);
                        event.getLevelRenderer().renderHitOutline(
                                poseStack,
                                buffer,
                                cameraPos.x,
                                cameraPos.y,
                                cameraPos.z,
                                renderState,
                                outlineColor,
                                Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState.appropriateLineWidth
                        );
                    }
                }
                return false;
            });
        }
    }

    @SubscribeEvent
    public static void registerCustomModels(RegisterItemModelsEvent event) {
        event.register(Utils.rl("bore_head"), BoreHeadModel.Unbaked.MAP_CODEC);
        event.register(Utils.rl("bore_item"), BoreItemSpecialRenderer.Unbaked.MAP_CODEC);
        event.register(Utils.rl("bore_wrapper"), BoreModelWrapper.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BoreContents.class, ClientBoresTooltip::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SPARK_PARTICLE.get(), SparkParticleProvider::new);
    }


    @SubscribeEvent
    public static void tickBoreProgress(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof BoreItem bore) {
            lastProgress = usingProgress;
            if (Minecraft.getInstance().options.keyAttack.isDown() || BoresClientConfig.CONFIG.BREAK_WITH_USE_KEY.get() && Minecraft.getInstance().options.keyUse.isDown()) {
                usingProgress = Math.min(usingProgress + 1, 10);
            } else {
                usingProgress = Math.max(usingProgress - 1, 0);
            }

            boolean isUsed = Utils.isUsed(stack);
            if (usingProgress < 9) {
                if (isUsed) {
                    ClientPacketDistributor.sendToServer(new SetUseBorePacket(false));
                }
            } else {
                if (!isUsed) {
                    ClientPacketDistributor.sendToServer(new SetUseBorePacket(true));
                }
                bore.onAttackTick(player.level(), player, stack, usingProgress);
                ClientPacketDistributor.sendToServer(new TickBorePacket(usingProgress));
            }
        }
    }

    @SubscribeEvent
    public static void disableAttack(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack()) {
            Player player = Minecraft.getInstance().player;
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof BoreItem) {
                event.setSwingHand(false);
                if (usingProgress <= 9 || Utils.getBoreContents(stack).isEmpty()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerItemRenderers(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return !entityLiving.getMainHandItem().isEmpty() && !entityLiving.getOffhandItem().isEmpty() ?
                        ModEnums.BORE_SINGLE_STANDING_POS.getValue() : ModEnums.BORE_STANDING_POS.getValue();
            }
        }, ModItems.BORE);
    }

    public static void handleTick(Level level, Player player, ItemStack stack) {
        BlockHitResult result = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (result.getType() == HitResult.Type.BLOCK) {
            if (BoresClientConfig.CONFIG.BREAKING_SOUNDS.get()) {
                if (ModClientEvents.soundInstance == null || !Minecraft.getInstance().getSoundManager().isActive(ModClientEvents.soundInstance)) {
                    if (ModClientEvents.idleSoundInstance != null) {
                        ModClientEvents.idleSoundInstance.remove();
                        ModClientEvents.idleSoundInstance2.remove();
                    }
                    ModClientEvents.soundInstance = new BoreSoundInstance(ModSounds.STONE.get(), SoundSource.PLAYERS, 0.25f, 1f, player, player.getRandom().nextLong());
                    ModClientEvents.soundInstance2 = new BoreSoundInstance(ModSounds.STONE.get(), SoundSource.PLAYERS, 0.25f, 1f, player, player.getRandom().nextLong());
                    Minecraft.getInstance().getSoundManager().play(ModClientEvents.soundInstance);
                    Minecraft.getInstance().getSoundManager().playDelayed(ModClientEvents.soundInstance2, 4);
                }
            }
            //Minecraft.getInstance().particleEngine.addBlockHitEffects(result.getBlockPos(), result);
            spawnSparks(level, player, result);
        } else {
            if (BoresClientConfig.CONFIG.BREAKING_SOUNDS.get()) {
                if (ModClientEvents.idleSoundInstance == null || !Minecraft.getInstance().getSoundManager().isActive(ModClientEvents.idleSoundInstance)) {
                    if (ModClientEvents.soundInstance != null) {
                        ModClientEvents.soundInstance.remove();
                        ModClientEvents.soundInstance2.remove();
                    }
                    ModClientEvents.idleSoundInstance = new BoreSoundInstance(ModSounds.AIR.get(), SoundSource.PLAYERS, 0.25f, 1f, player, player.getRandom().nextLong());
                    ModClientEvents.idleSoundInstance2 = new BoreSoundInstance(ModSounds.AIR.get(), SoundSource.PLAYERS, 0.25f, 1f, player, player.getRandom().nextLong());
                    Minecraft.getInstance().getSoundManager().play(ModClientEvents.idleSoundInstance);
                    Minecraft.getInstance().getSoundManager().playDelayed(ModClientEvents.idleSoundInstance2, 5);
                }
            }
        }
    }

    private static void spawnSparks(Level level, Player player, BlockHitResult hitResult) {
        if (level.getBlockState(hitResult.getBlockPos()).getDestroySpeed(level, hitResult.getBlockPos()) < 1.1) return;

        Vec3 hitPos = hitResult.getLocation();
        Vec3 playerEye = player.getEyePosition();
        Direction blockFace = hitResult.getDirection();

        Vec3 offset = Vec3.atLowerCornerOf(blockFace.getUnitVec3i()).scale(0.05);
        Vec3 spawnPos = hitPos.add(offset);

        int sparkCount = BoresClientConfig.CONFIG.PARTICLE_COUNT.get() + level.getRandom().nextInt(BoresClientConfig.CONFIG.PARTICLE_COUNT.get());

        for (int i = 0; i < sparkCount; i++) {
            double spreadX = (level.getRandom().nextDouble() - 0.5) * 0.15;
            double spreadY = (level.getRandom().nextDouble() - 0.5) * 0.15;
            double spreadZ = (level.getRandom().nextDouble() - 0.5) * 0.15;

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
