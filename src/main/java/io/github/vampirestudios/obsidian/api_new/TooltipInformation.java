package io.github.vampirestudios.obsidian.api_new;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class TooltipInformation {

    public String type;
    public String text;
    public String color = "0xFFFFFF";
    public String[] formatting = new String[0];

    public Text getTextType(String text) {
        String color1 = color.replace("#", "").replace("0x", "");
        switch (type) {
            case "literal":
            default:
                LiteralText literalText = new LiteralText(text);
                for(String formatting1 : formatting) {
                    literalText = (LiteralText) literalText.formatted(Formatting.byName(formatting1));
                }
                literalText = (LiteralText) literalText.setStyle(literalText.getStyle().withColor(new TextColor(Integer.parseInt(color1, 16))));
                return literalText;
            case "translable":
                TranslatableText translatableText = new TranslatableText(text);
                for(String formatting1 : formatting) {
                    translatableText = (TranslatableText) translatableText.formatted(Formatting.byName(formatting1));
                }
                translatableText = (TranslatableText) translatableText.setStyle(translatableText.getStyle().withColor(new TextColor(Integer.parseInt(color1, 16))));
                return translatableText;
        }
    }

}
