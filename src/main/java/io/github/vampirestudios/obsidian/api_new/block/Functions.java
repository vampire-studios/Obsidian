package io.github.vampirestudios.obsidian.api_new.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public class Functions {

    public static final MapCodec<Functions> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Identifier.CODEC.fieldOf("random_tick").forGetter((weather) -> {
            return weather.random_tick;
        }), Identifier.CODEC.fieldOf("scheduled_tick").forGetter((weather) -> {
            return weather.scheduled_tick;
        }), Identifier.CODEC.fieldOf("on_use").forGetter((weather) -> {
            return weather.on_use;
        }), Identifier.CODEC.fieldOf("random_display_tick").forGetter((weather) -> {
            return weather.random_display_tick;
        })).apply(instance, Functions::new);
    });

    public Identifier random_tick;
    public Identifier scheduled_tick;
    public Identifier on_use;
    public Identifier random_display_tick;

    public Functions(Identifier random_tick, Identifier scheduled_tick, Identifier on_use, Identifier random_display_tick) {
        this.random_tick = random_tick;
        this.scheduled_tick = scheduled_tick;
        this.on_use = on_use;
        this.random_display_tick = random_display_tick;
    }

}