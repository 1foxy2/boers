package net.foxy.bores.client;

import net.foxy.bores.item.BoreContents;
import net.foxy.bores.util.Utils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class ClientBoresTooltip implements ClientTooltipComponent {
    private static final Identifier SLOT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_background");
    private final BoreContents contents;

    public ClientBoresTooltip(BoreContents contents) {
        this.contents = contents;
    }

    @Override
    public int getHeight(Font font) {
        return 24;
    }

    @Override
    public int getWidth(Font font) {
        return 24;
    }

    @Override
    public boolean showTooltipWithItemInHand() {
        return true;
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        this.extractBundleWithItemsTooltip(font, x, y, w, h, graphics);
    }

    private void extractBundleWithItemsTooltip(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        ItemStack shownItems = this.contents.itemCopy();
        this.extractSlot(x, y, shownItems, font, graphics);
    }

    private void extractSlot(int drawX, int drawY, ItemStack itemStack, Font font, GuiGraphicsExtractor graphics) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, drawX, drawY, 24, 24);
        if (itemStack.isEmpty()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Utils.rl("container/bore/slot"), drawX + 3, drawY + 3, 18, 20);
        } else {
            graphics.item(itemStack, drawX + 4, drawY + 4, 0);
            graphics.itemDecorations(font, itemStack, drawX + 4, drawY + 4);
        }
    }
}
