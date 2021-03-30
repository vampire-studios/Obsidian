package io.github.vampirestudios.obsidian.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class ConfigAddonsListScreen extends Screen {

    public ConfigAddonsListScreen(Screen previousGui) {
        super(new TranslatableText("config_packs.title"));
    }

    @Override
    protected void init() {
        super.init();
    }

}