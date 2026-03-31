package net.foxy.bores.base;

import net.foxy.bores.BoresMod;
import net.foxy.bores.item.BoreItem;
import net.foxy.bores.item.BoreHeadItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BoresMod.MODID);

    public static final RegistryObject<BoreItem> BORE = ITEMS.register("bore", BoreItem::new);
    public static final RegistryObject<Item> BORE_HEAD = ITEMS.register("bore_head", () ->
            new BoreHeadItem(new Item.Properties().durability(1)));
}
