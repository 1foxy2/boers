package net.foxy.bores;

import net.foxy.bores.base.*;
import net.foxy.bores.data.BoreColoring;
import net.foxy.bores.util.Utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BoresMod.MODID)
public class BoresMod {
    public static final String MODID = "bores";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BoresMod(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModDataComponents.COMPONENTS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModRecipeSerializers.SERIALIZERS.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        modEventBus.addListener(BoresMod::buildCreativeTabs);
        modContainer.registerConfig(ModConfig.Type.COMMON, BoresConfig.CONFIG_SPEC);
    }

    public static void buildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.getParameters().holders().lookupOrThrow(ModRegistries.BORE_HEAD).listElements().forEach(boreHeadReference -> {
                event.insertAfter(Items.NETHERITE_HOE.getDefaultInstance(), Utils.bore(boreHeadReference), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
            for (DyeColor color : BoreColoring.ALLOWED_COLORS) {
                ItemStack stack = ModItems.BORE.toStack();
                stack.set(DataComponents.BASE_COLOR, color);
                event.insertAfter(Items.NETHERITE_HOE.getDefaultInstance(), stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }
}
