package net.foxy.bores.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.foxy.bores.data.BoreHead;
import net.foxy.bores.item.BoreContents;
import net.foxy.bores.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(
            method = "extractBlockDestroyAnimation",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER)
    )
    private void destroyAnim(Camera camera, LevelRenderState levelRenderState, CallbackInfo ci,
                             @Local BlockPos pos, @Local int progress) {
        if (Minecraft.getInstance().hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getBlockPos().equals(pos)) {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

            ItemStack stack = player.getWeaponItem();
            if (stack.isEmpty()) {
                return;
            }
            BoreContents boreContents = Utils.getBoreContentsOrEmpty(stack);
            if (boreContents.isEmpty()) {
                return;
            }
            Level level = player.level();

            BoreHead tool = Utils.getBore(boreContents.getItem());
            if (tool != null && tool.radius().isPresent()) {
                Utils.forEachBlock(level, player, pos, tool.radius().get(), (target, state) ->
                        levelRenderState.blockBreakingRenderStates
                                .add(new BlockBreakingRenderState(target, state, progress)));
            }
        }
    }
}
