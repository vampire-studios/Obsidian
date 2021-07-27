package io.github.vampirestudios.obsidian.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

public class ConfigAddonsButtonWidget extends ButtonWidget {

    public ConfigAddonsButtonWidget(int x, int y, int width, int height, String text, Screen screen) {
        super(x, y, width, height, new LiteralText(text), button -> MinecraftClient.getInstance().setScreen(new ConfigAddonsListScreen(screen)));
    }

}