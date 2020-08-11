package io.github.vampirestudios.obsidian.api_new;

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
import java.util.List;
import java.util.Map;

public class NameInformation {

    public static final MapCodec<NameInformation> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Identifier.CODEC.fieldOf("id").forGetter((weather) -> {
            return weather.id;
        }), TranslatableName.CODEC.fieldOf("translated").forGetter((weather) -> {
            return weather.translated;
        }), Codec.STRING.listOf().fieldOf("color").forGetter((weather) -> {
            return weather.color;
        }), Identifier.CODEC.fieldOf("formatting").forGetter((weather) -> {
            return weather.formatting;
        })).apply(instance, NameInformation::new);
    });

    public Identifier id;
    public List<TranslatableName> translated;
    public String color;
    public String[] formatting;

    public NameInformation(Identifier id, List<TranslatableName> translated, String color, String[] formatting) {
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
