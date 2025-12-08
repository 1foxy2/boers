package net.foxy.drills.base;

import net.foxy.drills.DrillsMod;
import net.foxy.drills.data.DrillHead;
import net.foxy.drills.event.ModEvents;
import net.foxy.drills.item.DrillContents;
import net.foxy.drills.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, DrillsMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TEXTURE =
            TABS.register("texture",
                    () -> CreativeModeTab.builder().icon(() -> {
                        ItemStack itemStack = ModItems.DRILL_BASE.toStack();
                        itemStack.set(ModDataComponents.DRILL_CONTENTS, new DrillContents(ModItems.DRILL_HEAD.toStack()));
                        return itemStack;
                            })
                            .title(Component.translatable("item.drills.drills"))
                            .displayItems((pParameters, pOutput) -> {
                                pOutput.accept(ModItems.DRILL_BASE);
                                pParameters.holders().lookupOrThrow(ModRegistries.DRILL_HEAD).listElements().forEach(drillHeadReference -> {
                                    pOutput.accept(Utils.drill(drillHeadReference));
                                });
                            })
                            .build());
}
