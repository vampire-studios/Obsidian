package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.KeyBinding;

public class KeybindingImpl extends net.minecraft.client.KeyMapping {

    public KeybindingImpl(KeyBinding keyBinding) {
        super(keyBinding.translationKey, keyBinding.getType(), keyBinding.getKeyCode(), keyBinding.categoryTranslationKey);
    }

}