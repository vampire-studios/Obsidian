package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.obsidian.ui.GUI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class CustomButton extends Button {
	private final WidgetSprites widgetSprites;
	private GUI.Widget widget;

	public static CustomButton.Builder builder1(Component message, Button.OnPress onPress) {
		return new CustomButton.Builder(message, onPress);
	}

	protected CustomButton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration, WidgetSprites widgetSprites) {
		super(x, y, width, height, message, onPress, createNarration);
		this.widgetSprites = widgetSprites;
	}

	public CustomButton setWidget(GUI.Widget widget) {
		this.widget = widget;
		return this;
	}

	@Override
	protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		Minecraft minecraft = Minecraft.getInstance();
//		guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
//		RenderSystem.enableBlend();
//		RenderSystem.enableDepthTest();
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, widgetSprites.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
//		guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		int i = this.active ? widget.activeColor : widget.defaultTextColor;
		this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final Component message;
		private final Button.OnPress onPress;
		@Nullable
		private Tooltip tooltip;
		private int x;
		private int y;
		private int width = 150;
		private int height = 20;
		private Button.CreateNarration createNarration = Button.DEFAULT_NARRATION;
		private WidgetSprites widgetSprites;

		public Builder(Component message, Button.OnPress onPress) {
			this.message = message;
			this.onPress = onPress;
		}

		public CustomButton.Builder pos(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public CustomButton.Builder width(int width) {
			this.width = width;
			return this;
		}

		public CustomButton.Builder size(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public CustomButton.Builder bounds(int x, int y, int width, int height) {
			return this.pos(x, y).size(width, height);
		}

		public CustomButton.Builder tooltip(@Nullable Tooltip tooltip) {
			this.tooltip = tooltip;
			return this;
		}

		public CustomButton.Builder createNarration(Button.CreateNarration createNarration) {
			this.createNarration = createNarration;
			return this;
		}

		public Builder widgetSprites(WidgetSprites widgetSprites) {
			this.widgetSprites = widgetSprites;
			return this;
		}

		public CustomButton build() {
			CustomButton button = new CustomButton(this.x, this.y, this.width, this.height, this.message, this.onPress, this.createNarration, widgetSprites);
			button.setTooltip(this.tooltip);
			return button;
		}
	}
}
