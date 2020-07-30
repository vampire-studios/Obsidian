package io.github.vampirestudios.obsidian.api_new.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.obsidian.api.DisplayInformation;
import net.minecraft.world.biome.Biome;

public class Block {

    public static final MapCodec<Block> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(BlockInformation.CODEC.fieldOf("information").forGetter((weather) -> {
            return weather.information;
        }), Codec.FLOAT.fieldOf("temperature").forGetter((weather) -> {
            return weather.temperature;
        }), Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", Biome.TemperatureModifier.NONE).forGetter((weather) -> {
            return weather.temperatureModifier;
        }), Codec.FLOAT.fieldOf("downfall").forGetter((weather) -> {
            return weather.downfall;
        })).apply(instance, Block::new);
    });

    public BlockInformation information;
    public DisplayInformation display;
    public AdditionalBlockInformation additional_information;
    public Functions functions;
    public OreInformation ore_information;

    public Block(BlockInformation information, DisplayInformation display, AdditionalBlockInformation additional_information, Functions functions, OreInformation ore_information) {
        this.information = information;
        this.display = display;
        this.additional_information = additional_information;
        this.functions = functions;
        this.ore_information = ore_information;
    }

}
