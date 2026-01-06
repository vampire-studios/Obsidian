/*
package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PanelWidget extends AbstractWidget {
    private final List<LayoutElement> children = new ArrayList<>();

    // Spacing between children
    private final int spacing;
    private GridLayout layout;
    private GridLayout.RowHelper rowHelper;
    private Identifier texture;

    public PanelWidget(int x, int y, int width, int height, int spacing) {
        super(x, y, width, height, null);
        this.spacing = spacing;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    public void setLayout(GridLayout layout) {
        this.layout = layout;
    }

    public void setRowHelper(GridLayout.RowHelper rowHelper) {
        this.rowHelper = rowHelper;
    }

    public void addChild(LayoutElement child) {
        this.rowHelper.addChild(child);
    }

    public List<LayoutElement> getChildren() {
        return this.rowHelper.addChild();
    }

    @Override
    protected void renderWidget(GuiGraphics poseStack, int mouseX, int mouseY, float partialTick) {
        // Optionally draw a background for the panel
        poseStack.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xAA000000);

        // Render each child
        layout.visitChildren(child -> {
            if (child instanceof AbstractWidget widget) {
                widget.render(poseStack, mouseX, mouseY, partialTick);
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Let children handle clicks
        for (LayoutElement child : children) {
            if (child instanceof AbstractWidget widget && widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (LayoutElement child : children) {
            if (child instanceof AbstractWidget widget && widget.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}*/
