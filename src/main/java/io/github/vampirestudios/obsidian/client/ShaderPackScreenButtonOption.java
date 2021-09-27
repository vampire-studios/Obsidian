package io.github.vampirestudios.obsidian.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.text.TranslatableText;

public class ShaderPackScreenButtonOption extends Option {
    private final Screen parent;
    private final MinecraftClient client;

    public ShaderPackScreenButtonOption(Screen parent, MinecraftClient client) {
        super("options.iris.shaderPackSelection");
        this.parent = parent;
        this.client = client;
    }

    @Override
    public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
        return new ButtonWidget(x, y, width, 20, new TranslatableText("options.iris.shaderPackSelection"), button -> this.client.openScreen(new ConfigAddonsListScreen(this.parent)));
    }
}