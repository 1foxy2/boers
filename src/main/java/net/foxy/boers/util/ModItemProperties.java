package net.foxy.boers.util;

import net.foxy.boers.base.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.Tag;

public class ModItemProperties {
    public static void addModItemProperties() {
        ItemProperties.register(ModItems.BOER_BASE.get(), Utils.rl("color"),
                (stack, level, entity, seed) -> {
            int color;
                    if (stack.hasTag() && stack.getTag().contains("color", Tag.TAG_INT)) {
                        color = stack.getTag().getInt("color");
                    } else {
                        color = 11;
                    }
                    if (color == 11) {
                        return -1;
                    }
                    return color;
                });
    }

}
