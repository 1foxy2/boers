package net.foxy.boers;

import com.mojang.logging.LogUtils;
import net.foxy.boers.base.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BoersMod.MODID)
public class BoersMod {
    public static final String MODID = "boers";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BoersMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        //ModDataComponents.COMPONENTS.register(modEventBus);
        //ModCreativeModeTabs.TABS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModRecipeSerializers.SERIALIZERS.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        modEventBus.addListener(BoersMod::buildCreativeTabs);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            BoersModClient.init(context);
        }
    }

    public static void buildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            // event.insertAfter();
            event.getParameters().holders().lookupOrThrow(ModRegistries.BOER_HEAD).listElements().forEach(boerHeadReference -> {
                event.insertAfter(Items.NETHERITE_HOE.getDefaultInstance(), Utils.boer(boerHeadReference), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
            for (DyeColor color : BoerColoring.ALLOWED_COLORS) {
                ItemStack stack = ModItems.BOER_BASE.toStack();
                stack.set(DataComponents.BASE_COLOR, color);
                event.insertAfter(Items.NETHERITE_HOE.getDefaultInstance(), stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }
}
