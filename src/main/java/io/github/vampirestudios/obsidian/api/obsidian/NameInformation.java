package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class NameInformation extends SpecialText {

    public Identifier id;

    public Text getName(String type) {
        return this.getTranslation(type);
    }

    public Text getTranslation(String type) {
        String color1 = !this.color.isEmpty() && !this.color.isBlank() ? color.replace("#", "").replace("0x", "") : "ffffff";
        if (id != null) {
            MutableText translatableText = Text.translatable(String.format(type + ".%s.%s", id.getNamespace(), id.getPath()));
            for (String formatting1 : formatting) {
                translatableText = translatableText.formatted(Formatting.byName(formatting1));
            }
            if (!this.color.isEmpty() && !this.color.isBlank()) {
                translatableText = translatableText.setStyle(translatableText.getStyle().withColor(TextColor.parse(color1)));
            }
            return translatableText;
        } else {
            return Text.literal("");
        }
    }

}