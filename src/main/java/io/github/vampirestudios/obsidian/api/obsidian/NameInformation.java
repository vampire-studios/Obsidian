package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public class NameInformation extends SpecialText {

    public ResourceLocation id;

    public Component getName(String type) {
        return this.getTranslation(type);
    }

    public Component getTranslation(String type) {
        String color1 = !this.color.isEmpty() && !this.color.isBlank() ? color.replace("#", "").replace("0x", "") : "ffffff";
        if (id != null) {
            MutableComponent translatableText = Component.translatable(String.format(type + ".%s.%s", id.getNamespace(), id.getPath()));
            for (String formatting1 : formatting) {
                translatableText = translatableText.withStyle(ChatFormatting.getByName(formatting1));
            }
            if (!this.color.isEmpty() && !this.color.isBlank()) {
                translatableText = translatableText.setStyle(translatableText.getStyle().withColor(TextColor.parseColor(color1)));
            }
            return translatableText;
        } else {
            return Component.literal("");
        }
    }

}