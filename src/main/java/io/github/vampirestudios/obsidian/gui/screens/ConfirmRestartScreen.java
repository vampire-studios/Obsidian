package io.github.vampirestudios.obsidian.gui.screens;

import io.github.vampirestudios.obsidian.gui.GridScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;


@Environment(EnvType.CLIENT)
public class ConfirmRestartScreen extends GridScreen {
    private final Text description;
    private final ConfirmRestartScreen.Listener listener;

    public ConfirmRestartScreen(ConfirmRestartScreen.Listener listener) {
        this(listener, null);
    }

    public ConfirmRestartScreen(ConfirmRestartScreen.Listener listener, Text message) {
        super(30, new TranslatableText("obsidian.datafixer.confirmrestart.title"));

        this.description = message==null?new TranslatableText("obsidian.datafixer.confirmrestart.message"):message;
        this.listener = listener;
    }

    protected void initLayout() {
        final int BUTTON_HEIGHT = 20;

        grid.addMessageRow(this.description, 25);

        grid.startRow();
        grid.addButton( BUTTON_HEIGHT, ScreenTexts.PROCEED, (button) -> {
            listener.proceed();
        });

        grid.endRow();
        grid.recenterVertically();
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public interface Listener {
        void proceed();
    }
}