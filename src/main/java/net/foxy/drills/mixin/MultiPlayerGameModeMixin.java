package net.foxy.drills.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.foxy.drills.base.ModItems;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @WrapWithCondition(
            method = "stopDestroyBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V")
    )
    private static boolean dontReset(LocalPlayer instance) {
        return !instance.getMainHandItem().is(ModItems.DRILL_BASE);
    }
}
