package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.text.Text;

public class TooltipInformation {

	public NameInformation text;

	public Text getTextType(String type) {
		return text.getName(type);
	}

}
