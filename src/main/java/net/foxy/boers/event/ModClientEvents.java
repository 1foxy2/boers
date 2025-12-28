package net.foxy.boers.event;

import net.foxy.boers.BoersMod;
import net.foxy.boers.base.*;
import net.foxy.boers.client.BoersClientConfig;
import net.foxy.boers.client.ClientBoersTooltip;
import net.foxy.boers.client.BoerBaseRenderer;
import net.foxy.boers.client.BoerSoundInstance;
import net.foxy.boers.client.model.BoerModel;
import net.foxy.boers.item.BoerBaseItem;
import net.foxy.boers.item.BoerContents;
import net.foxy.boers.network.c2s.SetUseBoerPacket;
import net.foxy.boers.network.c2s.TickBoerPacket;
import net.foxy.boers.particle.spark.SparkParticle;
import net.foxy.boers.particle.spark.SparkParticleProvider;
import net.foxy.boers.util.ModItemProperties;
import net.foxy.boers.util.Utils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = BoersMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    private static final ResourceLocation BOER_MODEL_LOADER = Utils.rl("boer");
    public static int lastProgress = 0;
    public static int usingProgress = 0;
    public static BoerSoundInstance soundInstance = null;
    public static BoerSoundInstance soundInstance2 = null;
    public static BoerSoundInstance idleSoundInstance = null;
    public static BoerSoundInstance idleSoundInstance2 = null;

    @SubscribeEvent
    public static void registerCustomModels(ModelEvent.RegisterGeometryLoaders event) {
        event.register(BOER_MODEL_LOADER, BoerModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BoerContents.class, ClientBoersTooltip::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SPARK_PARTICLE.get(), SparkParticleProvider::new);
    }


    @SubscribeEvent
    public static void tickBoerProgress(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof BoerBaseItem boer) {
            lastProgress = usingProgress;
            if (Minecraft.getInstance().options.keyAttack.isDown()) {
                usingProgress = Math.min(usingProgress + 1, 10);
            } else {
                usingProgress = Math.max(usingProgress - 1, 0);
            }

            boolean isUsed = Utils.isUsed(stack);
            if (usingProgress < 9) {
                if (isUsed) {
                    PacketDistributor.sendToServer(new SetUseBoerPacket(false));
                }
            } else {
                if (!isUsed) {
                    PacketDistributor.sendToServer(new SetUseBoerPacket(true));
                }
                boer.onAttackTick(player.level(), player, stack, usingProgress);
                PacketDistributor.sendToServer(new TickBoerPacket(usingProgress));
            }
        }
    }

    @SubscribeEvent
    public static void disableAttack(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack()) {
            Player player = Minecraft.getInstance().player;
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof BoerBaseItem boer) {
                event.setSwingHand(false);
                if (usingProgress <= 9) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerItemRenderers(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
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
                return ModEnums.BOER_STANDING_POS.getValue();
            }
        }, ModItems.BOER_BASE);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModItemProperties.addModItemProperties();
    }

    public static void handleTick(Level level, Player player, ItemStack stack) {
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
    }

    private static void spawnSparks(Level level, Player player, BlockHitResult hitResult) {
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
