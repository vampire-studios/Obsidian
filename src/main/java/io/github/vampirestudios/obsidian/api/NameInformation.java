package io.github.vampirestudios.obsidian.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.obsidian.api_new.block.BlockInformation;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class NameInformation {

    public static final MapCodec<NameInformation> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Identifier.CODEC.fieldOf("id").forGetter((weather) -> {
            return weather.id;
        }), ListCodec.INT.fieldOf("name").forGetter((weather) -> {
            return weather.name;
        }), Codec.STRING.fieldOf("item_group").forGetter((weather) -> {
            return weather.item_group;
        }), Identifier.CODEC.fieldOf("random_display_tick").forGetter((weather) -> {
            return weather.random_display_tick;
        })).apply(instance, NameInformation::new);
    });

    public Identifier id;
    public Map<String, String> translated;
    public String color;
    public String[] formatting;

    public NameInformation(Identifier id, Map<String, String> translated, String color, String[] formatting) {
        Biome
        this.id = id;
        this.translated = translated;
        this.color = color;
        this.formatting = formatting;
    }

    public Text getName(boolean block) {
        String color1 = color.replace("#", "").replace("0x", "");
        TranslatableText translatableText = new TranslatableText(String.format(block ? "block.%s.%s" : "item.%s.%s", id.getNamespace(), id.getPath()));
        for(String formatting1 : formatting) {
            translatableText.formatted(Formatting.byName(formatting1));
        }
        translatableText.setStyle(translatableText.getStyle().withColor(new TextColor(Integer.parseInt(color1, 16))));
        return translatableText;
    }

}
