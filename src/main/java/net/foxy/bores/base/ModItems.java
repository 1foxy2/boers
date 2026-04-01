package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.foxy.bores.item.BoreItem;
import net.foxy.bores.item.BoreHeadItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BoresMod.MODID);

    public static final DeferredItem<BoreItem> BORE = ITEMS.registerItem("bore", BoreItem::new);
    public static final DeferredItem<Item> BORE_HEAD = ITEMS.registerItem("bore_head", properties ->
            new BoreHeadItem(properties.durability(1)));
}
