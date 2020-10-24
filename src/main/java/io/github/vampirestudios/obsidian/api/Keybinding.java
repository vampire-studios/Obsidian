package io.github.vampirestudios.obsidian.api;

import io.github.vampirestudios.obsidian.utils.ConstantUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;

public class Keybinding {

    public String translationKey;
    public String categoryTranslationKey;
    public String type;
    public String keyCode;

    public InputUtil.Type getType() {
        return parseInputType(type);
    }
    public int getKeyCode() {
        return ConstantUtil.getKey(keyCode);
    }

    @Environment(EnvType.CLIENT)
    public static InputUtil.Type parseInputType(String string) {
        InputUtil.Type t;
        try {
            if (string == null) {
                return InputUtil.Type.KEYSYM;
            }
            t = InputUtil.Type.valueOf(string);
        } catch (IllegalArgumentException ex) {
            t = InputUtil.Type.KEYSYM;
        }
        return t;
    }

}
