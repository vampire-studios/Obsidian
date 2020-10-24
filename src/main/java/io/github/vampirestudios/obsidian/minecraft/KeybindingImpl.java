package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.Keybinding;
import net.minecraft.client.options.KeyBinding;

public class KeybindingImpl extends KeyBinding {

    public KeybindingImpl(Keybinding block) {
        super(block.translationKey, block.getType(), block.getKeyCode(), block.categoryTranslationKey);
    }

}