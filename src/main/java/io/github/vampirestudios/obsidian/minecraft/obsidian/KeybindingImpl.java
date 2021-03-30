package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.Keybinding;
import net.minecraft.client.option.KeyBinding;

public class KeybindingImpl extends KeyBinding {

    public KeybindingImpl(Keybinding block) {
        super(block.translationKey, block.getType(), block.getKeyCode(), block.categoryTranslationKey);
    }

}