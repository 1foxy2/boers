package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.foxy.bores.item.BoerBaseItem;
import net.foxy.bores.item.BoerHeadItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BoresMod.MODID);

    public static final DeferredItem<BoerBaseItem> BOER_BASE = ITEMS.register("bore", BoerBaseItem::new);
    public static final DeferredItem<Item> BOER_HEAD = ITEMS.register("bore_head", () ->
            new BoerHeadItem(new Item.Properties().durability(1)));
}
