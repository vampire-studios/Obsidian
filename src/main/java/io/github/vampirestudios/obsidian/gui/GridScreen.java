package io.github.vampirestudios.obsidian.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class GridScreen extends Screen {
	protected GridLayout grid = null;
	public final int topStart;
	
	public GridScreen(int topStart, Text title) {
		super(title);
		this.topStart = topStart;
	}
	
	final protected void init() {
		super.init();
		this.grid = new GridLayout(topStart, this.width, this.height, this.textRenderer, this::addDrawableChild);
		initLayout();
		grid.finalizeLayout();
	}
	
	protected abstract void initLayout();
	
	public void render(MatrixStack poseStack, int i, int j, float f) {
		//this.renderBackground(poseStack);
		this.renderBackground(poseStack, i);
		drawCenteredText(poseStack, this.textRenderer, this.title, grid.width / 2, grid.getTopStart(), 16777215);
		if (grid!=null) grid.render(poseStack);
		super.render(poseStack, i, j, f);
	}
}