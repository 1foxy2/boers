package net.foxy.drills.event;

import net.foxy.drills.DrillsMod;
import net.foxy.drills.base.ModRegistries;
import net.foxy.drills.data.DrillHead;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = DrillsMod.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void registerDrillManager(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModRegistries.DRILL_HEAD, DrillHead.CODEC, DrillHead.CODEC);
    }

}
