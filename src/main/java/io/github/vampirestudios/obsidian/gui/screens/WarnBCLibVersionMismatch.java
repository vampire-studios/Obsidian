package io.github.vampirestudios.obsidian.gui.screens;

import io.github.vampirestudios.obsidian.gui.GridScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class WarnBCLibVersionMismatch extends GridScreen {
	private final Text description;
	private final Listener listener;
	public WarnBCLibVersionMismatch(Listener listener) {
		super(30, new TranslatableText("obsidian.datafixer.bclibmissmatch.title"));
		
		this.description = new TranslatableText("obsidian.datafixer.bclibmissmatch.message");
		this.listener = listener;
	}
	
	protected void initLayout() {
		final int BUTTON_HEIGHT = 20;
		
		grid.addMessageRow(this.description, 25);
		
		grid.startRow();
		grid.addButton( BUTTON_HEIGHT, ScreenTexts.NO, (button) -> {
			listener.proceed(false);
		});
		grid.addButton( BUTTON_HEIGHT, ScreenTexts.YES, (button) -> {
			listener.proceed(true);
		});
		
		grid.endRow();
		grid.recenterVertically();
	}
	
	public boolean shouldCloseOnEsc() {
		return false;
	}
	
	@Environment(EnvType.CLIENT)
	public interface Listener {
		void proceed(boolean download);
	}
}