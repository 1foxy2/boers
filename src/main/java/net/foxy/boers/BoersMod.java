package net.foxy.boers;

import net.foxy.boers.base.*;
import net.foxy.boers.client.BoersClientConfig;
import net.foxy.boers.data.BoerColoring;
import net.foxy.boers.util.Utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BoersMod.MODID)
public class BoersMod {
    public static final String MODID = "boers";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BoersMod(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModDataComponents.COMPONENTS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModRecipeSerializers.SERIALIZERS.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModConditions.CONDITION_CODECS.register(modEventBus);
        modEventBus.addListener(BoersMod::buildCreativeTabs);
        modContainer.registerConfig(ModConfig.Type.COMMON, BoersConfig.CONFIG_SPEC);
    }

    public static void buildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
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
