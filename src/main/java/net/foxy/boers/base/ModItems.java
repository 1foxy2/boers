package net.foxy.boers.base;

import net.foxy.boers.BoersMod;
import net.foxy.boers.item.BoerBaseItem;
import net.foxy.boers.item.BoerHeadItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BoersMod.MODID);

    public static final RegistryObject<BoerBaseItem> BOER_BASE = ITEMS.register("boer_base", BoerBaseItem::new);
    public static final RegistryObject<Item> BOER_HEAD = ITEMS.register("boer_head", () ->
            new BoerHeadItem(new Item.Properties().durability(1)));
}
