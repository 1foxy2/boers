package net.foxy.boers.base;

import net.foxy.boers.BoresMod;
import net.foxy.boers.item.BoreContents;
import net.foxy.boers.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, BoresMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TEXTURE =
            TABS.register("texture",
                    () -> CreativeModeTab.builder().icon(() -> {
                        ItemStack itemStack = ModItems.BORE.toStack();
                        Utils.setBoreContents(itemStack, new BoreContents(ModItems.BORE_HEAD.toStack()));
                        return itemStack;
                            })
                            .title(Component.translatable("item.bores.bores"))
                            .displayItems((pParameters, pOutput) -> {
                                pOutput.accept(ModItems.BORE);
                                pParameters.holders().lookupOrThrow(ModRegistries.BORE_HEAD).listElements().forEach(boreHeadReference -> {
                                    pOutput.accept(Utils.bore(boreHeadReference));
                                });
                            })
                            .build());
}
