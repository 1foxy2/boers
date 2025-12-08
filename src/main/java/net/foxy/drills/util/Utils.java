package net.foxy.drills.util;

import net.foxy.drills.DrillsMod;
import net.foxy.drills.base.ModDataComponents;
import net.foxy.drills.base.ModItems;
import net.foxy.drills.data.DrillHead;
import net.foxy.drills.event.ModEvents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class Utils {
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(DrillsMod.MODID, path);
    }


    public static ItemStack drill(Holder<DrillHead> head) {
        ItemStack drill = ModItems.DRILL_HEAD.toStack();
        drill.set(ModDataComponents.DRILL, head);
        return drill;
    }

    public static DrillHead getDrill(ItemStack stack) {
        Holder<DrillHead> drillHead = stack.get(ModDataComponents.DRILL.get());

        return drillHead == null ? null : drillHead.value();
    }

    public static DrillHead drillOrDefault(ItemStack stack) {
        DrillHead head = getDrill(stack);
        return head == null ? DrillHead.DEFAULT : head;
    }
}
