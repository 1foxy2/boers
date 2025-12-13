package net.foxy.boers;

import net.foxy.boers.base.ModEnums;
import net.foxy.boers.client.BoersClientConfig;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BoersModClient {

    public static void init(FMLJavaModLoadingContext context) {
       // modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        context.registerConfig(ModConfig.Type.CLIENT, BoersClientConfig.CONFIG_SPEC);
        ModEnums.init();
    }
}
