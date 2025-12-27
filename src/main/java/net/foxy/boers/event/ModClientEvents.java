package net.foxy.boers.event;

import net.foxy.boers.BoersMod;
import net.foxy.boers.base.ModDataComponents;
import net.foxy.boers.base.ModEnums;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.base.ModParticles;
import net.foxy.boers.client.ClientBoersTooltip;
import net.foxy.boers.client.BoerBaseRenderer;
import net.foxy.boers.client.BoerSoundInstance;
import net.foxy.boers.client.model.BoerModel;
import net.foxy.boers.item.BoerBaseItem;
import net.foxy.boers.item.BoerContents;
import net.foxy.boers.network.c2s.SetUseBoerPacket;
import net.foxy.boers.network.c2s.TickBoerPacket;
import net.foxy.boers.particle.spark.SparkParticleProvider;
import net.foxy.boers.util.ModItemProperties;
import net.foxy.boers.util.Utils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
                return Utils.getDouble(itemStack) ? ModEnums.BOER_STANDING_POS.getValue() : ModEnums.BOER_SINGLE_STANDING_POS.getValue();
            }
        }, ModItems.BOER_BASE);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModItemProperties.addModItemProperties();
    }
}
