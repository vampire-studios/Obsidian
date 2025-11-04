/*
package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.awt.*;

public class ColorPickerWidget extends AbstractWidget {
    private float hue;        // 0..1
    private float saturation; // 0..1
    private float brightness; // 0..1, we’ll keep it at 1 for example

    private boolean dragging = false;

    public ColorPickerWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.hue = 0.0f;
        this.saturation = 1.0f;
        this.brightness = 1.0f;
    }

    public int getColor() {
        // Convert HSB to RGB
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    @Override
    protected void renderWidget(GuiGraphics poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        // We'll draw a gradient from left->right for Hue, top->bottom for Saturation
        // For each row, we compute hue. For each column, we compute saturation.
        // But that’s expensive per pixel. A simpler approach:
        // 1) Draw vertical strips for each hue x
        // 2) Each strip is a gradient from saturation=0..1
        // For a real mod, you might generate a dynamic texture and draw that.

        for (int px = 0; px < this.width; px++) {
            float localHue = (float) px / (this.width - 1);  // 0..1
            int startColor = Color.HSBtoRGB(localHue, 0f, 1f);
            int endColor   = Color.HSBtoRGB(localHue, 1f, 1f);
            // Draw vertical gradient
            for (int py = 0; py < this.height; py++) {
                float frac = (float)py / (this.height - 1);
                // Lerp from startColor to endColor
                int color = lerpColor(startColor, endColor, frac);
                // putPixel-like approach with fill
                poseStack.fill(this.getX() + px, this.getY() + py, this.getX() + px + 1, this.getY() + py + 1, color);
            }
        }

        // Draw an indicator for current selection
        int indicatorX = this.getX() + (int)(this.hue * (this.width - 1));
        int indicatorY = this.getX() + (int)((1.0f - this.saturation) * (this.height - 1));
        poseStack.fill(indicatorX - 2, indicatorY - 2, indicatorX + 3, indicatorY + 3, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseOver(mouseX, mouseY)) {
            updateColor(mouseX, mouseY);
            dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            updateColor(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateColor(double mouseX, double mouseY) {
        float localX = (float)(mouseX - this.getX());
        float localY = (float)(mouseY - this.getY());

        this.hue = Mth.clamp(localX / (this.width - 1), 0f, 1f);
        // Saturation is inverted top->bottom
        this.saturation = 1.0f - Mth.clamp(localY / (this.height - 1), 0f, 1f);
    }

    private int lerpColor(int c1, int c2, float t) {
        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;
        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;

        int r = (int)(r1 + (r2 - r1) * t);
        int g = (int)(g1 + (g2 - g1) * t);
        int b = (int)(b1 + (b2 - b1) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}*/
