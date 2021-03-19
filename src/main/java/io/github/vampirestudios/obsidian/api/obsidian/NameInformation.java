package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NameInformation extends SpecialText {

	public Identifier id;

	public Text getName(String type) {
		return this.getName(id, type);
	}

}