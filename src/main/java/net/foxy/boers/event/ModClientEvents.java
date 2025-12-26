package net.foxy.boers.event;

import net.foxy.boers.BoersMod;
import net.foxy.boers.base.ModEnums;
import net.foxy.boers.base.ModItems;
import net.foxy.boers.base.ModParticles;
import net.foxy.boers.client.ClientBoersTooltip;
import net.foxy.boers.client.BoerBaseRenderer;
import net.foxy.boers.client.BoerSoundInstance;
import net.foxy.boers.client.model.BoerModel;
import net.foxy.boers.item.BoerBaseItem;
import net.foxy.boers.item.BoerTooltip;
import net.foxy.boers.network.NetworkHandler;
import net.foxy.boers.network.c2s.SetUseBoerPacket;
import net.foxy.boers.network.c2s.TickBoerPacket;
import net.foxy.boers.particle.spark.SparkParticleProvider;
import net.foxy.boers.util.ModItemProperties;
import net.foxy.boers.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;

public class ModClientEvents {
    public static int lastProgress = 0;
    public static int usingProgress = 0;
    public static BoerSoundInstance soundInstance = null;
    public static BoerSoundInstance soundInstance2 = null;
    public static BoerSoundInstance idleSoundInstance = null;
    public static BoerSoundInstance idleSoundInstance2 = null;

    @Mod.EventBusSubscriber(modid = BoersMod.MODID, value = Dist.CLIENT)
    public static class ForgeBus {

        @SubscribeEvent
        public static void tickBoerProgress(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
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
                        NetworkHandler.INSTANCE.sendToServer(new SetUseBoerPacket(false));
                    }
                } else {
                    if (!isUsed) {
                        NetworkHandler.INSTANCE.sendToServer(new SetUseBoerPacket(true));
                    }
                    boer.onAttackTick(player.level(), player, stack, usingProgress);
                    NetworkHandler.INSTANCE.sendToServer(new TickBoerPacket(usingProgress));
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
    }

    @Mod.EventBusSubscriber(modid = BoersMod.MODID, value = Dist.CLIENT, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {

        @SubscribeEvent
        public static void registerCustomModels(ModelEvent.RegisterGeometryLoaders event) {
            event.register("boer", BoerModel.Loader.INSTANCE);
        }

        @SubscribeEvent
        public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
             event.register(BoerTooltip.class, ClientBoersTooltip::new);
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
}
