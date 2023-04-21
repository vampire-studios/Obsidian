package io.github.vampirestudios.obsidian.api.obsidian;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.vampirestudios.obsidian.utils.ConstantUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

public class KeyBinding {

    public ResourceLocation id;
    public String translationKey;
    public String categoryTranslationKey;
    public String type;
    public String keyCode;

    @Environment(EnvType.CLIENT)
    public static InputConstants.Type parseInputType(String string) {
        InputConstants.Type t;
        try {
            if (string == null) {
                return InputConstants.Type.KEYSYM;
            }
            t = InputConstants.Type.valueOf(string);
        } catch (IllegalArgumentException ex) {
            t = InputConstants.Type.KEYSYM;
        }
        return t;
    }

    public InputConstants.Type getType() {
        return parseInputType(type);
    }

    public int getKeyCode() {
        return ConstantUtil.getKey(keyCode);
    }

}
