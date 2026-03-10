package io.github.vampirestudios.obsidian.registry.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnergyStorage(long stored, long capacity) {
    public static final Codec<EnergyStorage> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.fieldOf("stored").forGetter(EnergyStorage::stored),
            Codec.LONG.fieldOf("capacity").forGetter(EnergyStorage::capacity)
    ).apply(i, EnergyStorage::new));

    public boolean hasEnergy() { return stored > 0; }
    public double fillRatio()  { return capacity <= 0 ? 0.0 : (double) stored / (double) capacity; }
}
