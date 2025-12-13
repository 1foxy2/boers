package net.foxy.boers.base;

import net.foxy.boers.BoersMod;
import net.foxy.boers.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), BoersMod.MODID);

    public static final RegistryObject<CreativeModeTab> TEXTURE =
            TABS.register("texture",
                    () -> CreativeModeTab.builder().icon(() -> {
                        ItemStack itemStack = ModItems.BOER_BASE.get().getDefaultInstance();
                        Utils.setBoerContents(itemStack, ModItems.BOER_HEAD.get().getDefaultInstance());
                        return itemStack;
                            })
                            .title(Component.translatable("item.boers.boers"))
                            .displayItems((pParameters, pOutput) -> {
                                pOutput.accept(ModItems.BOER_BASE.get());
                                pParameters.holders().lookupOrThrow(ModRegistries.BOER_HEAD).listElements().forEach(boerHeadReference -> {
                                    pOutput.accept(Utils.boer(boerHeadReference));
                                });
                            })
                            .build());
}
