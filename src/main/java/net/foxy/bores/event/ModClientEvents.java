package net.foxy.bores.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.foxy.bores.BoresMod;
import net.foxy.bores.base.ModParticles;
import net.foxy.bores.base.ModSounds;
import net.foxy.bores.client.BoreSoundInstance;
import net.foxy.bores.client.BoresClientConfig;
import net.foxy.bores.client.ClientBoresTooltip;
import net.foxy.bores.client.model.BoreModel;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.item.BoerTooltip;
import net.foxy.bores.item.BoreItem;
import net.foxy.bores.network.NetworkHandler;
import net.foxy.bores.network.c2s.SetUseBorePacket;
import net.foxy.bores.network.c2s.TickBorePacket;
import net.foxy.bores.particle.spark.SparkParticle;
import net.foxy.bores.particle.spark.SparkParticleProvider;
import net.foxy.bores.util.ModItemProperties;
import net.foxy.bores.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ModClientEvents {
    public static int lastProgress = 0;
    public static int usingProgress = 0;
    public static BoreSoundInstance soundInstance = null;
    public static BoreSoundInstance soundInstance2 = null;
    public static BoreSoundInstance idleSoundInstance = null;
    public static BoreSoundInstance idleSoundInstance2 = null;

    @SubscribeEvent
    public static void renderOutline(RenderHighlightEvent.Block event) {
        if (!(event.getCamera().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            return;
        }
        ItemStack boreContents = Utils.getBoreContents(stack);
        if (boreContents.isEmpty()) {
            return;
        }
        Level level = event.getCamera().getEntity().level();

        BoreHead tool = Utils.getBore(Utils.getBoreContentsOrEmpty(stack));
        int k = Minecraft.getInstance().gameMode.getDestroyStage();
        if (tool != null && tool.radius().isPresent()) {
            Vec3 vec3 = event.getCamera().getPosition();
            PoseStack posestack = event.getPoseStack();
            VertexConsumer vertexconsumer2 = event.getMultiBufferSource().getBuffer(RenderType.lines());
            Utils.forEachBlock(level, player, event.getTarget().getBlockPos(), tool.radius().get(), (target, block) -> {
                event.getLevelRenderer().renderHitOutline(posestack,
                        vertexconsumer2, player, vec3.x(), vec3.y(), vec3.z(), target, block);

                if (k != -1 && event.getTarget().getBlockPos().equals(Minecraft.getInstance().gameMode.destroyBlockPos)) {
                    posestack.pushPose();
                    posestack.translate((double) target.getX() - vec3.x, (double) target.getY() - vec3.y, (double) target.getZ() - vec3.z);
                    PoseStack.Pose posestack$pose1 = posestack.last();
                    VertexConsumer vertexconsumer1 = new SheetedDecalTextureGenerator(
                            Minecraft.getInstance().renderBuffers().crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(k)), posestack$pose1.pose(), posestack$pose1.normal(), 1.0F
                    );
                    ModelData modelData = level.getModelDataManager().getAt(target);
                    Minecraft.getInstance()
                            .getBlockRenderer()
                            .renderBreakingTexture(block, target, level, posestack, vertexconsumer1, modelData == null ? net.minecraftforge.client.model.data.ModelData.EMPTY : modelData);
                    posestack.popPose();
                }
            });
        }
    }

    @Mod.EventBusSubscriber(modid = BoresMod.MODID, value = Dist.CLIENT)
    public static class ForgeBus {

        @SubscribeEvent
        public static void tickBoreProgress(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
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
                        NetworkHandler.INSTANCE.sendToServer(new SetUseBorePacket(false));
                    }
                } else {
                    if (!isUsed) {
                        NetworkHandler.INSTANCE.sendToServer(new SetUseBorePacket(true));
                    }
                    bore.onAttackTick(player.level(), player, stack, usingProgress);
                    NetworkHandler.INSTANCE.sendToServer(new TickBorePacket(usingProgress));
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
    }

    @Mod.EventBusSubscriber(modid = BoresMod.MODID, value = Dist.CLIENT, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {

        @SubscribeEvent
        public static void registerCustomModels(ModelEvent.RegisterGeometryLoaders event) {
            event.register("bore", BoreModel.Loader.INSTANCE);
        }

        @SubscribeEvent
        public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
             event.register(BoerTooltip.class, ClientBoresTooltip::new);
        }

        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticles.SPARK_PARTICLE.get(), SparkParticleProvider::new);
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            ModItemProperties.addModItemProperties();
        }
    }

    public static void handleTick(Level level, Player player, ItemStack stack) {
        BlockHitResult result = Utils.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
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
            Minecraft.getInstance().particleEngine.addBlockHitEffects(result.getBlockPos(), result);
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

        Vec3 offset = Vec3.atLowerCornerOf(blockFace.getNormal()).scale(0.05);
        Vec3 spawnPos = hitPos.add(offset);

        int sparkCount = BoresClientConfig.CONFIG.PARTICLE_COUNT.get() + level.random.nextInt(BoresClientConfig.CONFIG.PARTICLE_COUNT.get());

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

