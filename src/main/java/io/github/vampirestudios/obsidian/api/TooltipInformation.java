package io.github.vampirestudios.obsidian.api;

import net.minecraft.text.Text;

public class TooltipInformation {

    public NameInformation text;

    public Text getTextType() {
        return text.getName(false);
    }

}
