package io.github.vampirestudios.obsidian.api.builders;

import io.github.vampirestudios.obsidian.api.parsers.ThingParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

public class SoundTypeBuilder extends BaseBuilder<SoundType, SoundTypeBuilder> {
    public static SoundTypeBuilder begin(ThingParser<SoundTypeBuilder> ownerParser, ResourceLocation registryName) {
        return new SoundTypeBuilder(ownerParser, registryName);
    }

    private float volume = 1.0f;
    private float pitch = 1.0f;
    private ResourceLocation breakSound;
    private ResourceLocation stepSound;
    private ResourceLocation placeSound;
    private ResourceLocation hitSound;
    private ResourceLocation fallSound;

    private SoundTypeBuilder(ThingParser<SoundTypeBuilder> ownerParser, ResourceLocation registryName) {
        super(ownerParser, registryName);
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setBreakSound(ResourceLocation resourceLocation) {
        breakSound = resourceLocation;
    }

    public void setStepSound(ResourceLocation resourceLocation) {
        stepSound = resourceLocation;
    }

    public void setPlaceSound(ResourceLocation placeSound) {
        this.placeSound = placeSound;
    }

    public void setHitSound(ResourceLocation resourceLocation) {
        hitSound = resourceLocation;
    }

    public void setFallSound(ResourceLocation resourceLocation) {
        fallSound = resourceLocation;
    }

    @Override
    protected String getThingTypeDisplayName() {
        return "Sound Type";
    }

    @Override
    protected SoundType buildInternal() {
        SoundEvent breakSoundEvent = BuiltInRegistries.SOUND_EVENT.get(breakSound);
        SoundEvent stepSoundEvent = BuiltInRegistries.SOUND_EVENT.get(stepSound);
        SoundEvent placeSoundEvent = BuiltInRegistries.SOUND_EVENT.get(placeSound);
        SoundEvent hitSoundEvent = BuiltInRegistries.SOUND_EVENT.get(hitSound);
        SoundEvent fallSoundEvent = BuiltInRegistries.SOUND_EVENT.get(fallSound);
        return new SoundType(volume, pitch, breakSoundEvent, stepSoundEvent, placeSoundEvent, hitSoundEvent, fallSoundEvent);
    }
}