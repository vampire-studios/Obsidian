package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.network.chat.Component;

public class TooltipInformation {

	public SpecialText text;

	public Component getText() {
		return text.getName();
	}

}
