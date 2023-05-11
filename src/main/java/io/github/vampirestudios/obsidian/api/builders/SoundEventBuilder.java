package io.github.vampirestudios.obsidian.api.builders;

import io.github.vampirestudios.obsidian.api.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilder extends BaseBuilder<SoundEvent, SoundEventBuilder> {
    public static SoundEventBuilder begin(ThingParser<SoundEventBuilder> ownerParser, ResourceLocation registryName) {
        return new SoundEventBuilder(ownerParser, registryName);
    }

    private Float range;

    private SoundEventBuilder(ThingParser<SoundEventBuilder> ownerParser, ResourceLocation registryName) {
        super(ownerParser, registryName);
    }

    public void setRange(Float range) {
        this.range = range;
    }

    @Override
    protected String getThingTypeDisplayName() {
        return "Sound Event";
    }

    @Override
    protected SoundEvent buildInternal() {
        return range != null
                ? SoundEvent.createFixedRangeEvent(getRegistryName(), range)
                : SoundEvent.createVariableRangeEvent(getRegistryName());
    }
}