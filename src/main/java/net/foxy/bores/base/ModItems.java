package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.foxy.bores.item.BoreItem;
import net.foxy.bores.item.BoreHeadItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BoresMod.MODID);

    public static final DeferredItem<BoreItem> BORE = ITEMS.register("bore", BoreItem::new);
    public static final DeferredItem<Item> BORE_HEAD = ITEMS.register("bore_head", () ->
            new BoreHeadItem(new Item.Properties().durability(1)));
}
