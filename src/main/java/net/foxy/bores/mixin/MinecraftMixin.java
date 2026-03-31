package net.foxy.bores.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.foxy.bores.base.ModItems;
import net.foxy.bores.client.BoresClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Final
    public Options options;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @ModifyExpressionValue(
            method = "handleKeybinds()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 13)
    )
    public boolean breakWithUseKey(boolean original) {
        if (BoresClientConfig.CONFIG.BREAK_WITH_USE_KEY.get() && player.getMainHandItem().is(ModItems.BORE.get()) && options.keyUse.consumeClick()) {
            return true;
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "handleKeybinds()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z", ordinal = 4)
    )
    public boolean continueBreakWithUseKey(boolean original) {
        if (BoresClientConfig.CONFIG.BREAK_WITH_USE_KEY.get() && player.getMainHandItem().is(ModItems.BORE.get()) && options.keyUse.isDown()) {
            return true;
        }

        return original;
    }
}
