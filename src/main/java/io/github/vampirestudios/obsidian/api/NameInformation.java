package io.github.vampirestudios.obsidian.api;

import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Map;

public class NameInformation {

    public Identifier id;
    public Map<String, String> translated;
    public String color;
    public String[] formatting = new String[]{};

    public Text getName(boolean block) {
        String color1 = color.replace("#", "").replace("0x", "");
        TranslatableText translatableText = new TranslatableText(String.format(block ? "block.%s.%s" : "item.%s.%s", id.getNamespace(), id.getPath()));
        for(String formatting1 : formatting) {
            translatableText.formatted(Formatting.byName(formatting1));
        }
        translatableText.setStyle(translatableText.getStyle().withColor(new TextColor(Integer.parseInt(color1, 16))));
        return translatableText;
    }

}
