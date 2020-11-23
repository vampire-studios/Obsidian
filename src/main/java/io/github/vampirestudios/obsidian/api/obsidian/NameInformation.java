package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Map;

public class NameInformation {

    public Identifier id;
    public String text;
    public String type;
    public Map<String, String> translated;
    public String color;
    public String[] formatting = new String[0];

    public Text getName(boolean block) {
        String color1 = color.replace("#", "").replace("0x", "");
        if (!text.isEmpty()) {
            switch (type) {
                case "literal":
                    LiteralText literalText = new LiteralText(text);
                    for(String formatting1 : formatting) {
                        literalText = (LiteralText) literalText.formatted(Formatting.byName(formatting1));
                    }
                    literalText = (LiteralText) literalText.setStyle(literalText.getStyle().withColor(new TextColor(Integer.parseInt(color1, 16))));
                    return literalText;
                default:
                case "translable":
                    TranslatableText translatableText = new TranslatableText(text);
                    for(String formatting1 : formatting) {
                        translatableText = (TranslatableText) translatableText.formatted(Formatting.byName(formatting1));
                    }
                    translatableText = (TranslatableText) translatableText.setStyle(translatableText.getStyle().withColor(new TextColor(Integer.parseInt(color1, 16))));
                    return translatableText;
            }
        } else {
            TranslatableText translatableText = new TranslatableText(String.format(block ? "block.%s.%s" : "item.%s.%s", id.getNamespace(), id.getPath()));
            for(String formatting1 : formatting) {
                translatableText = (TranslatableText) translatableText.formatted(Formatting.byName(formatting1));
            }
            translatableText = (TranslatableText) translatableText.setStyle(translatableText.getStyle().withColor(new TextColor(Integer.parseInt(color1, 16))));
            return translatableText;
        }
    }

}