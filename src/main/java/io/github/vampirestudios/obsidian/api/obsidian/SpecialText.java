package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.annotations.SerializedName;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SpecialText {

    public String text;
    @SerializedName("type")
    public String textType;
    public Map<String, String> translated = new HashMap<>();
    public String color = "";
    public String[] formatting = new String[0];

    public Text getName(Identifier id, String type) {
        String color1 = !this.color.isEmpty() && !this.color.isBlank() ? color.replace("#", "").replace("0x", "") : "ffffff";
        if (!text.isEmpty()) {
            if ("literal".equals(textType)) {
                LiteralText literalText = new LiteralText(text);
                for (String formatting1 : formatting) {
                    literalText = (LiteralText) literalText.formatted(Formatting.byName(formatting1));
                }
                if (!this.color.isEmpty() && !this.color.isBlank()) {
                    literalText = (LiteralText) literalText.setStyle(literalText.getStyle().withColor(TextColor.parse(color1)));
                }
                return literalText;
            } else {
                TranslatableText translatableText = new TranslatableText(text);
                for (String formatting1 : formatting) {
                    translatableText = (TranslatableText) translatableText.formatted(Formatting.byName(formatting1));
                }
                if (!this.color.isEmpty() && !this.color.isBlank()) {
                    translatableText = (TranslatableText) translatableText.setStyle(translatableText.getStyle().withColor(TextColor.parse(color1)));
                }
                return translatableText;
            }
        } else {
            if (id != null) {
                TranslatableText translatableText = new TranslatableText(String.format(type + ".%s.%s", id.getNamespace(), id.getPath()));
                for (String formatting1 : formatting) {
                    translatableText = (TranslatableText) translatableText.formatted(Formatting.byName(formatting1));
                }
                if (!this.color.isEmpty() && !this.color.isBlank()) {
                    translatableText = (TranslatableText) translatableText.setStyle(translatableText.getStyle().withColor(TextColor.parse(color1)));
                }
                return translatableText;
            } else {
                return new LiteralText("");
            }
        }
    }

}
