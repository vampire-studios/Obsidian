package io.github.vampirestudios.obsidian.api;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class TooltipInformation {

    public String textType;
    public String text;
    public String[] formatting;

    public Text getTextType(String text) {
        switch (textType) {
            case "literal":
            default:
                LiteralText literalText = new LiteralText(text);
                for(String formatting1 : formatting) {
                    literalText.formatted(Formatting.byName(formatting1));
                }
                return literalText;
            case "translable":
                TranslatableText translatableText = new TranslatableText(text);
                for(String formatting1 : formatting) {
                    translatableText.formatted(Formatting.byName(formatting1));
                }
                return translatableText;
        }
    }

}
