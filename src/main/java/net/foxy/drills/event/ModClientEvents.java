package net.foxy.drills.event;

import net.foxy.drills.DrillsMod;
import net.foxy.drills.base.ModEnums;
import net.foxy.drills.base.ModItems;
import net.foxy.drills.base.ModParticles;
import net.foxy.drills.client.ClientDrillTooltip;
import net.foxy.drills.client.DrillBaseRenderer;
import net.foxy.drills.client.model.DrillModel;
import net.foxy.drills.item.DrillContents;
import net.foxy.drills.particle.spark.SparkParticleProvider;
import net.foxy.drills.util.ModItemProperties;
import net.foxy.drills.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = DrillsMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    private static final ResourceLocation DRILL_MODEL_LOADER = Utils.rl("drill");

    @SubscribeEvent
    public static void registerCustomModels(ModelEvent.RegisterGeometryLoaders event) {
        event.register(DRILL_MODEL_LOADER, DrillModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(DrillContents.class, ClientDrillTooltip::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SPARK_PARTICLE.get(), SparkParticleProvider::new);
    }

    @SubscribeEvent
    public static void registerItemRenderers(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            public static DrillBaseRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new DrillBaseRenderer(
                            Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                            Minecraft.getInstance().getEntityModels());
                }

                return renderer;
            }

            @Override
            public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return ModEnums.DRILL_STANDING_POS.getValue();
            }
        }, ModItems.DRILL_BASE);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModItemProperties.addModItemProperties();
    }
}
