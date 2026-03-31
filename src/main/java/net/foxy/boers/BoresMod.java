package net.foxy.boers;

import com.mojang.logging.LogUtils;
import net.foxy.boers.base.*;
import net.foxy.boers.data.BoreColoring;
import net.foxy.boers.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BoresMod.MODID)
public class BoresMod {
    public static final String MODID = "bores";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BoresMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModRecipeSerializers.SERIALIZERS.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModGlobalLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        modEventBus.addListener(BoresMod::buildCreativeTabs);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            BoresModClient.init(context);
        }

        ((ForgeRegistry<Item>) ForgeRegistries.ITEMS).addAlias(ResourceLocation.fromNamespaceAndPath("boers", "boer_base"), Utils.rl("bore"));
        ((ForgeRegistry<Item>) ForgeRegistries.ITEMS).addAlias(ResourceLocation.fromNamespaceAndPath("boers", "boer_head"), Utils.rl("bore_head"));
    }

    public static void buildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.getParameters().holders().lookupOrThrow(ModRegistries.BORE_HEAD).listElements().forEach(boerHeadReference -> {
                event.getEntries().putAfter(Items.NETHERITE_HOE.getDefaultInstance(), Utils.bore(boerHeadReference), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            });
            for (DyeColor color : BoreColoring.ALLOWED_COLORS) {
                ItemStack stack = ModItems.BORE.get().getDefaultInstance();
                stack.getOrCreateTag().putInt("color", color.getId());
                event.getEntries().putAfter(Items.NETHERITE_HOE.getDefaultInstance(), stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }
}
