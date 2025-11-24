package net.foxy.drills.event;

import net.foxy.drills.DrillsMod;
import net.foxy.drills.base.ModItems;
import net.foxy.drills.data.DrillManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = DrillsMod.MODID)
public class ModEvents {
    public static final DrillManager DRILL_MANAGER = new DrillManager();

    @SubscribeEvent
    public static void registerDrillManager(AddReloadListenerEvent event) {
        event.addListener(DRILL_MANAGER);
    }

}
