package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.platform.InputConstants;
import io.github.vampirestudios.obsidian.utils.ConstantUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class KeyBinding {

    public Identifier id;
    public String translationKey;
    public String type;
    public String keyCode;
    public String category;
    @SerializedName("custom_category")
    @blue.endless.jankson.annotation.SerializedName("custom_category")
    public Identifier customCategory;

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

    public KeyMapping.Category getCategory() {
        return switch(category) {
            case "movement" -> KeyMapping.Category.MOVEMENT;
            case "misc" -> KeyMapping.Category.MISC;
            case "multiplayer" -> KeyMapping.Category.MULTIPLAYER;
            case "gameplay" -> KeyMapping.Category.GAMEPLAY;
            case "inventory" -> KeyMapping.Category.INVENTORY;
            case "creative" -> KeyMapping.Category.CREATIVE;
            case "spectator" -> KeyMapping.Category.SPECTATOR;
            case "debug" -> KeyMapping.Category.DEBUG;
            case "custom" -> KeyMapping.Category.register(customCategory);
			default -> throw new IllegalStateException("Unexpected value: " + category);
		};
    }

}
