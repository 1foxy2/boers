package net.foxy.drills.base;

import net.foxy.drills.util.Utils;
import net.neoforged.neoforge.common.util.Lazy;
import net.foxy.drills.DrillsMod;
import net.foxy.drills.data.DrillHead;
import net.foxy.drills.item.DrillBaseItem;
import net.foxy.drills.item.DrillHeadItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DrillsMod.MODID);

    public static final DeferredItem<DrillBaseItem> DRILL_BASE = ITEMS.register("drill_base", DrillBaseItem::new);
    public static final DeferredItem<Item> DRILL_HEAD = ITEMS.register("drill_head", () ->
            new DrillHeadItem(new Item.Properties().component(ModDataComponents.DRILL_ID, Utils.rl("default")).durability(1)));
}
