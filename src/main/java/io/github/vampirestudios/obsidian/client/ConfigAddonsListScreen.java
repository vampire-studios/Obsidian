package io.github.vampirestudios.obsidian.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.stream.Stream;

public class ConfigAddonsListScreen extends Screen {
    private final Screen parent;

    private ShaderPackListWidget shaderPacks;
    private PackListWidget availablePackList;

    public ConfigAddonsListScreen(Screen previousGui) {
        super(new LiteralText("Obsidian Addon Information"));
        this.parent = previousGui;
    }

    @Override
    protected void init() {
        super.init();
//        this.shaderPacks = new ShaderPackListWidget(this.client, this.width / 2, this.height, 32, this.height - 58, 0, width / 2);
//        this.children.add(shaderPacks);
        availablePackList = new PackListWidget(this.client, 200, this.height, new TranslatableText("pack.available.title"));
        this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
        this.children.add(availablePackList);
    }

    private void updatePackList(PackListWidget widget, Stream<ResourcePackOrganizer.Pack> packs) {
        widget.children().clear();
        packs.forEach((pack) -> {
            widget.children().add(new PackListWidget.ResourcePackEntry(this.client, widget, this, pack));
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

//        this.shaderPacks.render(matrices, mouseX, mouseY, delta);
        this.availablePackList.render(matrices, mouseX, mouseY, delta);

        GuiUtil.drawDirtTexture(client, 0, 0, -100, width, 32);
        GuiUtil.drawDirtTexture(client, 0, this.height - 58, -100, width, 58);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
        drawCenteredText(matrices, this.textRenderer, new LiteralText("Obsidian Addons").formatted(Formatting.GRAY, Formatting.ITALIC), (int)(this.width * 0.25), 21, 16777215);
        drawCenteredText(matrices, this.textRenderer, new LiteralText("Addon Information").formatted(Formatting.GRAY, Formatting.ITALIC), (int)(this.width * 0.75), 21, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
}