package io.github.vampirestudios.obsidian.addonapi;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

import java.util.List;

public final class TextComponent {
    public String text;
    public String color;
    public String translate;
    public List<Object> with;             // args for translate

    public Component toMc() {
        if (translate != null) {
            MutableComponent base = Component.translatable(translate,
                    with != null ? with.toArray() : new Object[0]);
            if (color != null) {
                base.withStyle(style -> style.withColor(TextColor.parseColor(color).getOrThrow()));
            }
            return base;
        }
        MutableComponent base = Component.literal(text != null ? text : "");
        if (color != null) {
            base.withStyle(style -> style.withColor(TextColor.parseColor(color).getOrThrow()));
        }
        return base;
    }
}