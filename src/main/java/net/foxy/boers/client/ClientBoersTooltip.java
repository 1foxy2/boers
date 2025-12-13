package net.foxy.boers.client;

import net.foxy.boers.item.BoerTooltip;
import net.foxy.boers.util.Utils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientBoersTooltip implements ClientTooltipComponent {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("textures/gui/container/bundle.png");
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;
    private final ItemStack stack;

    public ClientBoersTooltip(BoerTooltip contents) {
        this.stack = contents.getItems();
    }

    @Override
    public int getHeight() {
        return this.backgroundHeight() + 4;
    }

    @Override
    public int getWidth(Font font) {
        return this.backgroundWidth();
    }

    private int backgroundWidth() {
        return this.gridSizeX() * 18 + 2;
    }

    private int backgroundHeight() {
        return this.gridSizeY() * 20 + 2;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int i = this.gridSizeX();
        int j = this.gridSizeY();
        guiGraphics.blit(BACKGROUND_SPRITE, x, y, 0, 0, 18, 20, 128, 128);
        int k = 0;

        int j1 = x + 1;
        int k1 = y + 1;
        this.renderSlot(j1, k1, k++, guiGraphics, font);
    }

    private void renderSlot(int x, int y, int itemIndex, GuiGraphics guiGraphics, Font font) {
        if (itemIndex >= 1) {
            this.blit(guiGraphics, x, y, Texture.SLOT);
        } else {
            ItemStack itemstack = this.stack;
            this.blit(guiGraphics, x, y, Texture.SLOT);
            guiGraphics.renderItem(itemstack, x + 1, y + 1, itemIndex);
            guiGraphics.renderItemDecorations(font, itemstack, x + 1, y + 1);
            if (itemIndex == 0) {
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, x + 1, y + 1, 0);
            }
        }
    }

    private void blit(GuiGraphics guiGraphics, int x, int y, Texture texture) {
       // guiGraphics.blitSprite(texture.sprite, x, y, 0, texture.w, texture.h);
    }

    private int gridSizeX() {
        return 1;
    }

    private int gridSizeY() {
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    static enum Texture {
        BLOCKED_SLOT(ResourceLocation.withDefaultNamespace("container/bundle/blocked_slot"), 18, 20),
        SLOT(Utils.rl("container/boer/slot"), 18, 20);

        public final ResourceLocation sprite;
        public final int w;
        public final int h;

        private Texture(ResourceLocation sprite, int w, int h) {
            this.sprite = sprite;
            this.w = w;
            this.h = h;
        }
    }
}
