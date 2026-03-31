package net.foxy.bores;

import net.foxy.bores.base.ModEnums;
import net.foxy.bores.client.BoresClientConfig;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BoresModClient {

    public static void init(FMLJavaModLoadingContext context) {
       // modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        context.registerConfig(ModConfig.Type.CLIENT, BoresClientConfig.CONFIG_SPEC);
        ModEnums.init();
    }
}
