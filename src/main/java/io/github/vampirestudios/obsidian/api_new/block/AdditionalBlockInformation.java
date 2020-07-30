package io.github.vampirestudios.obsidian.api_new.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class AdditionalBlockInformation {

    public static final MapCodec<AdditionalBlockInformation> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.BOOL.fieldOf("slab").forGetter((weather) -> {
            return weather.slab;
        }), Codec.BOOL.fieldOf("stairs").forGetter((weather) -> {
            return weather.stairs;
        }), Codec.BOOL.fieldOf("walls").forGetter((weather) -> {
            return weather.walls;
        }), Codec.BOOL.fieldOf("fence").forGetter((weather) -> {
            return weather.fence;
        }), Codec.BOOL.fieldOf("fenceGate").forGetter((weather) -> {
            return weather.fenceGate;
        }), Codec.BOOL.fieldOf("button").forGetter((weather) -> {
            return weather.button;
        }), Codec.BOOL.fieldOf("pressurePlate").forGetter((weather) -> {
            return weather.pressurePlate;
        }), Codec.BOOL.fieldOf("door").forGetter((weather) -> {
            return weather.door;
        }), Codec.BOOL.fieldOf("trapdoor").forGetter((weather) -> {
            return weather.trapdoor;
        }), Codec.BOOL.fieldOf("rotatable").forGetter((weather) -> {
            return weather.rotatable;
        }), Codec.BOOL.fieldOf("pillar").forGetter((weather) -> {
            return weather.pillar;
        })).apply(instance, AdditionalBlockInformation::new);
    });

    public boolean slab = false;
    public boolean stairs = false;
    public boolean walls = false;
    public boolean fence = false;
    public boolean fenceGate = false;
    public boolean button = false;
    public boolean pressurePlate = false;
    public boolean door = false;
    public boolean trapdoor = false;
    public boolean rotatable = false;
    public boolean pillar = false;

    public AdditionalBlockInformation(boolean slab, boolean stairs, boolean walls, boolean fence, boolean fenceGate, boolean button, boolean pressurePlate, boolean door, boolean trapdoor, boolean rotatable, boolean pillar) {
        this.slab = slab;
        this.stairs = stairs;
        this.walls = walls;
        this.fence = fence;
        this.fenceGate = fenceGate;
        this.button = button;
        this.pressurePlate = pressurePlate;
        this.door = door;
        this.trapdoor = trapdoor;
        this.rotatable = rotatable;
        this.pillar = pillar;
    }
}