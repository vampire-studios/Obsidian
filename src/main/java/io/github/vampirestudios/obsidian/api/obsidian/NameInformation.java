package io.github.vampirestudios.obsidian.api.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class NameInformation extends SpecialText {

    public static final Random RANDOM = new Random();

    public static final Codec<NameInformation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("text", "").forGetter(s -> s.text),
            Codec.STRING.optionalFieldOf("text_type", "").forGetter(s -> s.textType),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("translations", Map.of()).forGetter(s -> s.translations),
            ResourceLocation.CODEC.optionalFieldOf("id", ResourceLocation.withDefaultNamespace(STR."\{RANDOM.nextInt(10000)}")).forGetter(s -> s.id)
    ).apply(instance, NameInformation::new));

    public NameInformation(String text, String textType, Map<String, String> translations, ResourceLocation id) {
        super(text, textType, translations);
        this.id = id;
    }

    public NameInformation() {
    }

    public ResourceLocation id;

    public Component getName(String type) {
        if (id != null && Objects.equals(this.textType, "translatable")) {
			return Component.translatable(String.format(STR."\{type}.%s.%s", id.getNamespace(), id.getPath()));
        } else {
            return getName();
        }
    }

}