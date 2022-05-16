package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.KeyBinding;
import net.minecraft.client.option.KeyBind;

public class KeybindingImpl extends KeyBind {

    public KeybindingImpl(KeyBinding keyBinding) {
        super(keyBinding.translationKey, keyBinding.getType(), keyBinding.getKeyCode(), keyBinding.categoryTranslationKey);
    }

}