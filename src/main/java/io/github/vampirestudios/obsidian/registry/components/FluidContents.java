package io.github.vampirestudios.obsidian.registry.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FluidContents(IdOrTag fluidType, long amount) {
    public static final Codec<FluidContents> CODEC = RecordCodecBuilder.create(i -> i.group(
            IdOrTag.CODEC.fieldOf("fluid_type").forGetter(FluidContents::fluidType),
            Codec.LONG.fieldOf("amount").forGetter(FluidContents::amount)
    ).apply(i, FluidContents::new));
}
