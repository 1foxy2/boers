package net.foxy.drills.util;

import net.foxy.drills.event.ModEvents;
import net.neoforged.neoforge.common.util.Lazy;
import net.foxy.drills.DrillsMod;
import net.foxy.drills.base.ModDataComponents;
import net.foxy.drills.base.ModItems;
import net.foxy.drills.data.DrillHead;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class Utils {
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(DrillsMod.MODID, path);
    }


    public static ItemStack drill(DrillHead head) {
        ItemStack drill = ModItems.DRILL_HEAD.toStack();
        drill.set(ModDataComponents.DRILL, head);
        drill.set(ModDataComponents.DRILL_ID, head.id());
        return drill;
    }

    public static DrillHead getDrill(ItemStack stack) {
        DrillHead drillHead = stack.get(ModDataComponents.DRILL.get());

        ResourceLocation drillId = stack.get(ModDataComponents.DRILL_ID);
        if (drillHead == null || !drillHead.id().equals(drillId)) {
            if (drillId != null) {
                drillHead = ModEvents.DRILL_MANAGER.getDrillHead(drillId);
                stack.set(ModDataComponents.DRILL, drillHead);
            }
        }
        return drillHead;
    }

    public static DrillHead drillOrDefault(ItemStack stack) {
        DrillHead head = getDrill(stack);
        return head == null ? DrillHead.DEFAULT : head;
    }
}
