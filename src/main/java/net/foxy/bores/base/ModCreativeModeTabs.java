package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.foxy.bores.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), BoresMod.MODID);

    public static final RegistryObject<CreativeModeTab> TEXTURE =
            TABS.register("texture",
                    () -> CreativeModeTab.builder().icon(() -> {
                        ItemStack itemStack = ModItems.BORE_HEAD.get().getDefaultInstance();
                        Utils.setBoreContents(itemStack, ModItems.BORE_HEAD.get().getDefaultInstance());
                        return itemStack;
                            })
                            .title(Component.translatable("item.bores.bores"))
                            .displayItems((pParameters, pOutput) -> {
                                pOutput.accept(ModItems.BORE.get());
                                pParameters.holders().lookupOrThrow(ModRegistries.BORE_HEAD).listElements().forEach(boerHeadReference -> {
                                    pOutput.accept(Utils.bore(boerHeadReference));
                                });
                            })
                            .build());
}
