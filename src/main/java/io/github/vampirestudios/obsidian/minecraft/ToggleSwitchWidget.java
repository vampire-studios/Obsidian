/*
package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ToggleSwitchWidget extends AbstractWidget {
    private boolean toggled;
    private float thumbPosition;
    // goes from 0.0 (off) to 1.0 (on), used for animating the thumb

    private boolean draggingThumb = false;

    public ToggleSwitchWidget(int x, int y, int width, int height, boolean initialState) {
        super(x, y, width, height, Component.empty());
        this.toggled = initialState;
        this.thumbPosition = initialState ? 1.0f : 0.0f;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        // Optionally animate:
        this.thumbPosition = toggled ? 1.0f : 0.0f;
    }

    @Override
    protected void renderWidget(GuiGraphics poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        // Background track color
        int trackColor = this.toggled ? 0xFF00AA00 : 0xFF555555;
        poseStack.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, trackColor);

        // Animate thumb position if needed (smooth transitions)
        // e.g., we approach the target toggled state gradually
        float target = toggled ? 1.0f : 0.0f;
        float speed = 0.2f; // how fast it moves
        this.thumbPosition = this.thumbPosition + speed * (target - this.thumbPosition);

        // Calculate thumb position in pixels
        int thumbWidth = this.width / 2;
        int thumbX = (int)(this.getX() + (this.width - thumbWidth) * this.thumbPosition);

        // Draw the thumb
        int thumbColor = 0xFFFFFFFF; // white
        poseStack.fill(thumbX, this.getY(), thumbX + thumbWidth, this.getY() + this.height, thumbColor);

        // Optionally, draw some text in the center
        // e.g., "ON"/"OFF" or something else
        // This widget does not draw text by default (setMessage(...) is empty).
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button) && this.isMouseOver(mouseX, mouseY)) {
            // Start dragging
            this.draggingThumb = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.draggingThumb) {
            // Convert mouseX to [0, 1] range for thumbPosition
            double relativeX = mouseX - this.getX();
            double newPos = relativeX / (double) this.width;
            this.thumbPosition = (float)Math.max(0.0, Math.min(1.0, newPos));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.draggingThumb) {
            this.draggingThumb = false;
            // Determine if we ended more than halfway
			this.setToggled(this.thumbPosition >= 0.5f);
            // We could also fire an onPress() or callback here
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}*/
