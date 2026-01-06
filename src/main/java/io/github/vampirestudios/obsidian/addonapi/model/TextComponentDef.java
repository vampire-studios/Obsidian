package io.github.vampirestudios.obsidian.addonapi.model;

import com.google.gson.annotations.SerializedName;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.List;

public class TextComponentDef {

    public String text;
    public String color;
    public Boolean bold;
    public Boolean italic;
    public String translate;

    @SerializedName("with")
    public List<String> withArgs;

    public Component toMc() {
        Component base;
        if (translate != null) {
            Object[] args = withArgs != null ? withArgs.toArray() : new Object[0];
            base = Component.translatable(translate, args);
        } else {
            base = Component.literal(text != null ? text : "");
        }

        Style style = base.getStyle();
        if (color != null) {
            TextColor tc = TextColor.parseColor(color).getOrThrow();
			style = style.withColor(tc);
        }
        if (bold != null) style = style.withBold(bold);
        if (italic != null) style = style.withItalic(italic);

        return base.copy().setStyle(style);
    }
}
