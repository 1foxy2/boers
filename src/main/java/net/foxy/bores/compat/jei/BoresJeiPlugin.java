package net.foxy.bores.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.foxy.bores.base.ModItems;
import net.foxy.bores.util.Utils;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class BoresJeiPlugin implements IModPlugin {
    @Override
    public @NotNull Identifier getPluginUid() {
        return Utils.rl("main");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.BORE.asItem(), BoreSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(ModItems.BORE_HEAD.asItem(), BoreHeadSubtypeInterpreter.INSTANCE);
    }
}
