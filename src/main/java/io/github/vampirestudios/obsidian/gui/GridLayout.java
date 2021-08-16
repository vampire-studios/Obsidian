package io.github.vampirestudios.obsidian.gui;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class GridLayout {
	record LabelPos(MultilineText label, int top) {}
	record ButtonPos(float alpha, int top, int width, int height, Text component, ButtonWidget.PressAction onPress) {}

	public final int width;
	public final int height;
	@NotNull
	private final TextRenderer font;
	private final Consumer<ButtonWidget> addButtonFunction;
	private final int topStart;
	private int topOffset;
	private int top;
	private int currentRowHeight = 0;
	private int currentRowMargin = 6;
	private int lastRowMargin = 0;
	
	private final List<LabelPos> labels;
	private final List<List<ButtonPos>> buttons;
	private List<ButtonPos> currentButtonRow;
	
	public GridLayout(int topStart, int width, int height, TextRenderer font, Consumer<ButtonWidget> addButtonFunction){
		Objects.requireNonNull(font);
		this.topStart = topStart;
		top = topStart + 20;
		this.topOffset = 0;
		this.width = width;
		this.height = height;
		this.font = font;
		this.addButtonFunction = addButtonFunction;
		labels = new ArrayList<>(4);
		buttons = new ArrayList<>(4);
	}
	
	public int getTopStart(){
		return topStart + topOffset;
	}
	
	public void addMessageRow(Text text, int padding){
		addMessageRow(MultilineText.create(this.font, text, this.width - 2*padding));
	}
	
	public void addMessageRow(MultilineText lb){
		labels.add(new LabelPos(lb, top));
		int promptLines = lb.count() + 1;
		int height = promptLines * 9;
		
		currentRowMargin = 12;
		currentRowHeight = height;
	}
	
	public void startRow(){
		this.endRow();
		this.currentButtonRow = new ArrayList<>(8);
		this.buttons.add(this.currentButtonRow);
		
	}
	
	public void endRow(){
		lastRowMargin = currentRowMargin;
		top += currentRowHeight + currentRowMargin;
		currentRowHeight = 0;
		currentRowMargin = 0;
	}
	
	public void recenterVertically(){
		int hg = (top - lastRowMargin) - topStart;
		int targetTop = (height - hg)/2;
		topOffset = targetTop - topStart;
	}
	
	void finalizeLayout(){
		final int BUTTON_SPACING = 10;
		for (List<ButtonPos> row : this.buttons) {
			int count = row.size();
			int rowWidth = row.stream().map(b -> b.width).reduce(0, Integer::sum) + (count - 1) * BUTTON_SPACING;
			int left = (width - rowWidth) / 2;
			
			for (ButtonPos bp : row) {
				ButtonWidget customButton = new ButtonWidget(left, bp.top+topOffset, bp.width, bp.height, bp.component, bp.onPress);
				customButton.setAlpha(bp.alpha);
				addButtonFunction.accept(customButton);
				
				left += BUTTON_SPACING + bp.width;
			}
		}
	}
	
	public void addButton(int width, int height, Text component, ButtonWidget.PressAction onPress){
		addButton(1.0f, width, height, component, onPress);
	}
	
	public void addButton(int height, Text component, ButtonWidget.PressAction onPress){
		addButton(1.0f, height, component, onPress);
	}
	
	public void addButton(float alpha, int height, Text component, ButtonWidget.PressAction onPress){
		final int BUTTON_PADDING = 12;
		int width = font.getWidth(component.asOrderedText()) + 2*BUTTON_PADDING;
		addButton(alpha, width, height, component, onPress);
	}
	
	public void addButton(float alpha, int width, int height, Text component, ButtonWidget.PressAction onPress){
		currentRowHeight = Math.max(currentRowHeight, height);
		currentRowMargin = 6;
		currentButtonRow.add(new ButtonPos(alpha, top, width, height, component, onPress));
	}
	
	public void render(MatrixStack poseStack){
		labels.forEach(lp -> {
			lp.label.drawCenterWithShadow(poseStack, this.width / 2, lp.top + topOffset);
		});
	}
}