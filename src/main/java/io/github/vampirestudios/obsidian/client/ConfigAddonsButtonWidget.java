package io.github.vampirestudios.obsidian.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class ConfigAddonsButtonWidget extends ButtonWidget {

	public ConfigAddonsButtonWidget(int x, int y, int width, int height, String text, Screen screen) {
		super(x, y, width, height, text, button -> MinecraftClient.getInstance().openScreen(new ConfigAddonsListScreen(screen)));
	}

}