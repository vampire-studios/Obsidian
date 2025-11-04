package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.obsidian.ui.GUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ProgressBar extends AbstractWidget {
    private final ResourceLocation progressTexture;
    private double progress;  // Progress is a value between 0.0 (empty) and 1.0 (full)

    public ProgressBar(GUI.Widget widget) {
        super(widget.position.x, widget.position.y, widget.position.width, widget.position.height, widget.hasText ? Component.literal(widget.text) : Component.empty());
        this.progressTexture = widget.progressBar.texture;
        this.progress = 0.0;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, progressTexture, getX(), getY(), (int) (width * progress), height);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}