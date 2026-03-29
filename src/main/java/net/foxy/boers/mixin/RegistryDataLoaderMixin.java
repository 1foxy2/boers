package net.foxy.boers.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.foxy.boers.base.ModRegistries;
import net.foxy.boers.util.Utils;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {

    @WrapOperation(
            method = "loadContentsFromManager",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/FileToIdConverter;" +
                    "listMatchingResources(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"
            )
    )
    private static <E> Map<ResourceLocation, Resource> getOldIdDatapack(
            FileToIdConverter instance, ResourceManager resourceManager,
            Operation<Map<ResourceLocation, Resource>> original,
            @Local(argsOnly = true) WritableRegistry<E> registry
    ) {
        Map<ResourceLocation, Resource> map = original.call(instance, resourceManager);
        if (registry.key().equals(ModRegistries.BORE_HEAD)) {
            String s = Registries.elementsDirPath(ModRegistries.OLD_BOER_HEAD);
            FileToIdConverter filetoidconverter = FileToIdConverter.json(s);

            map = new HashMap<>(map);
            for (Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(resourceManager).entrySet()) {
                ResourceLocation resourcelocation = entry.getKey();
                resourcelocation = resourcelocation.withPath(resourcelocation.getPath().replace("boers/boer_head", "bores/bore_head"));
                if (resourcelocation.getNamespace().equals("boers")) {
                    resourcelocation = Utils.rl(resourcelocation.getPath());
                }
                map.put(resourcelocation, entry.getValue());
            }
        }
        return map;
    }

}
