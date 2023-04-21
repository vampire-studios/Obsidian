package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.network.chat.Component;

public class TooltipInformation {

    public NameInformation text;

    public Component getTextType(String type) {
        return text.getName(type);
    }

}
