package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ObsidianAddonInformationScreen extends Screen {

    private Screen parent;
    private ObsidianAddon addon;

    public ObsidianAddonInformationScreen(Screen parent, ObsidianAddon addon) {
        super(new LiteralText(addon.getDisplayNameObsidian() + " Information Screen"));
        this.parent = parent;
        this.addon = addon;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        GuiUtil.drawDirtTexture(client, 0, 0, -100, width, 32);
        GuiUtil.drawDirtTexture(client, 0, this.height - 58, -100, width, 58);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);

        drawTextWithShadow(matrices, this.textRenderer, new LiteralText("Description").formatted(Formatting.BOLD, Formatting.UNDERLINE), 4, 25, 16777215);
        drawTextWithShadow(matrices, this.textRenderer, new LiteralText(this.addon.getConfigPackInfo().description), 4, 37, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

}
