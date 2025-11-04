package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.obsidian.ui.GUI;
import io.github.vampirestudios.obsidian.client.CustomButton;
import io.github.vampirestudios.obsidian.client.ProgressBar;
import io.github.vampirestudios.obsidian.client.ToggleButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class JsonGui extends AbstractContainerScreen<DynamicContainer> {
    private final GUI gui;

    public JsonGui(DynamicContainer menu, Inventory inv, GUI gui) {
        super(menu, inv, Component.literal(gui.title));
        this.gui = gui;
		this.imageWidth = gui.textureWidth;
		this.imageHeight = gui.textureHeight;
    }

    @Override
    protected void init() {
        super.init();
        loadGuiFromJson();
    }

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, gui.texture, leftPos, topPos, this.imageWidth, this.imageHeight);
//		guiGraphics.blitSprite(RenderType::guiTextured, gui.texture, leftPos, topPos, this.imageWidth, this.imageHeight);
	}

	private void loadGuiFromJson() {
        for (GUI.Widget widget : gui.widgets) {
			if (widget == null) continue;

            int width = widget.position.width;
            int height = widget.position.height;
            int x = this.width / 2 - width / 2 + widget.position.x;
            int y = this.height / 2 - height / 2 + widget.position.y;

			Component text = widget.text == null || widget.text.isEmpty() ? Component.empty() : Component.literal(widget.text);
			switch (widget.type) {
				case "button" -> createButton(text, width, height, x, y, widget);
				case "checkbox" -> this.addRenderableWidget(Checkbox.builder(text, this.font).pos(x, y).build());
				case "edit_box" -> createEditBox(text, width, height, x, y, widget);
                case "string" -> {
					if (widget.customPosString)
						this.addRenderableWidget(new StringWidget(x, y, font.width(text.getVisualOrderText()), 9, text, font));
					else this.addRenderableWidget(new StringWidget(text, font));
				}
				case "progress_bar" -> this.addRenderableWidget(new ProgressBar(widget));
			}
        }
    }

    private void createButton(Component text, int width, int height, int x, int y, GUI.Widget widget) {
		Button.OnPress onPress = button -> {};
		switch (widget.buttonType) {
			case "basic" -> this.addRenderableWidget(CustomButton.builder1(text, onPress)
					.widgetSprites(widget.widgetSprites)
					.bounds(x, y, width, height)
					.build()
			);
			case "image" -> {
				if (widget.hasText)
					this.addRenderableWidget(new ImageButton(x, y, width, height, widget.widgetSprites, onPress, text));
				else this.addRenderableWidget(new ImageButton(x, y, width, height, widget.widgetSprites, onPress));
			}
			case "text" -> this.addRenderableWidget(new PlainTextButton(x, y, width, height, text, onPress, this.font));
			case "toggle" -> this.addRenderableWidget(new ToggleButton(x, y, width, height, widget));
		}
    }

    private void createEditBox(Component text, int width, int height, int x, int y, GUI.Widget widget) {
		Component placeholder = Component.literal(widget.placeholder);
        if (widget.editBox.multiLine)
			if (widget.editBox.fitting)
				this.addRenderableWidget(new FittingMultiLineTextWidget(x, y, width, height, placeholder, this.font)
						.setColor(widget.editBox.defaultTextColor)
				);
			else this.addRenderableWidget(MultiLineEditBox.builder().setPlaceholder(placeholder).build(this.font, width, height, text));
        else {
			EditBox editBox = new EditBox(this.font, x, y, width, height, text);
			editBox.setTextColor(widget.editBox.defaultTextColor);
			editBox.setTextColorUneditable(widget.editBox.disabledTextColor);
			editBox.setEditable(widget.editBox.editable);
			editBox.setBordered(widget.editBox.bordered);
			editBox.setMaxLength(widget.editBox.maxLength);
			this.addRenderableWidget(editBox);
		}
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }
}