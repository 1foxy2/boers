package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.foxy.bores.item.BoerContents;
import net.foxy.bores.util.Utils;
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
                        ItemStack itemStack = ModItems.BOER_BASE.toStack();
                        Utils.setBoerContents(itemStack, new BoerContents(ModItems.BOER_HEAD.toStack()));
                        return itemStack;
                            })
                            .title(Component.translatable("item.bores.bores"))
                            .displayItems((pParameters, pOutput) -> {
                                pOutput.accept(ModItems.BOER_BASE);
                                pParameters.holders().lookupOrThrow(ModRegistries.BOER_HEAD).listElements().forEach(boerHeadReference -> {
                                    pOutput.accept(Utils.boer(boerHeadReference));
                                });
                            })
                            .build());
}
