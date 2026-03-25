package net.foxy.bores.util;

import net.foxy.bores.base.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;

public class ModItemProperties {
    public static void addModItemProperties() {
        ItemProperties.register(ModItems.BOER_BASE.get(), Utils.rl("color"),
                (stack, level, entity, seed) -> {
                    int color = stack.getOrDefault(DataComponents.BASE_COLOR, DyeColor.BLUE).getId();
                    if (color == 11) {
                        return -1;
                    }
                    return color;
                });
    }

}
