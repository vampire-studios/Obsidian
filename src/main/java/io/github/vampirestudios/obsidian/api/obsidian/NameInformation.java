package io.github.vampirestudios.obsidian.api.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Objects;

public class NameInformation extends SpecialText {

    public static final Codec<NameInformation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("text", "").forGetter(s -> s.text),
            Codec.STRING.optionalFieldOf("text_type", "").forGetter(s -> s.textType),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("translations", Map.of()).forGetter(s -> s.translations)
    ).apply(instance, NameInformation::new));

    public NameInformation(String text, String textType, Map<String, String> translations) {
        super(text, textType, translations);
    }

    public NameInformation() {
    }

    public Component getName(String type, Identifier id) {
        if (id != null && Objects.equals(this.textType, "translatable")) {
			return Component.translatable(id.toLanguageKey(type));
        } else {
            return getName();
        }
    }

}