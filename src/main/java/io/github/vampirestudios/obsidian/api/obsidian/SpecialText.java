package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.annotations.SerializedName;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class SpecialText {

    public String text;
    @SerializedName("type")
    public String textType;
    public Map<String, String> translated = new HashMap<>();
    public String color = "";
    public String[] formatting = new String[0];

    public Text getName() {
        String color1 = !this.color.isEmpty() && !this.color.isBlank() ? color.replace("#", "").replace("0x", "") : "ffffff";
        if (!text.isEmpty()) {
            if ("literal".equals(textType)) {
                MutableText literalText = Text.literal(text);
                for (String formatting1 : formatting) {
                    literalText = literalText.formatted(Formatting.byName(formatting1));
                }
                if (!this.color.isEmpty() && !this.color.isBlank()) {
                    literalText = literalText.setStyle(literalText.getStyle().withColor(TextColor.parse(color1)));
                }
                return literalText;
            } else {
                MutableText translatableText = Text.translatable(text);
                for (String formatting1 : formatting) {
                    translatableText = translatableText.formatted(Formatting.byName(formatting1));
                }
                if (!this.color.isEmpty() && !this.color.isBlank()) {
                    translatableText = translatableText.setStyle(translatableText.getStyle().withColor(TextColor.parse(color1)));
                }
                return translatableText;
            }
        } else {
            return Text.literal("");
        }
    }

}
