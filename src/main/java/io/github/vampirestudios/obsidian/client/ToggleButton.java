package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.obsidian.ui.GUI;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ToggleButton extends Button {
    @Nullable
    protected WidgetSprites sprites;
    private final GUI.Widget widget;
    protected boolean isStateTriggered;

    public ToggleButton(int x, int y, int width, int height, GUI.Widget widget) {
        super(x, y, width, height, Component.empty(), button -> {}, Supplier::get);
        this.widget = widget;
        this.isStateTriggered = widget.defaultState;
        this.sprites = widget.widgetSprites;
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        super.onPress(inputWithModifiers);
        isStateTriggered = !isStateTriggered;
        setMessage(isStateTriggered ? Component.literal(widget.onText) : Component.literal(widget.offText));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.sprites != null) {
//            RenderSystem.disableDepthTest();
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprites.get(this.isStateTriggered, this.isHoveredOrFocused()), this.getX(), this.getY(), this.width, this.height);
//            guiGraphics.blitSprite(RenderType::guiTextured, this.sprites.get(this.isStateTriggered, this.isHoveredOrFocused()), this.getX(), this.getY(), this.width, this.height);
//            RenderSystem.enableDepthTest();
        }
    }
}