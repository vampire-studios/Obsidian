package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.annotations.SerializedName;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class SpecialText {
	public String text;
	@SerializedName("type")
	public String textType = "literal";
	public Map<String, String> translations;

	public SpecialText(String text, String textType, Map<String, String> translations) {
		this.text = text;
		this.textType = textType;
		this.translations = translations;
	}

	public SpecialText() {
	}

	public Component getName() {
		if (text != null && !text.isEmpty()) {
			if ("translatable".equals(textType)) {
				return Component.translatable(text);
			} else if ("space".equals(textType)) {
				return Component.literal("");
			} else {
				return TagParser.QUICK_TEXT.parseNode(text).toComponent();
			}
		} else {
			return Component.literal("");
		}
	}

}
