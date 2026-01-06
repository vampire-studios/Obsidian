package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class SoundEffect extends Effect {
    private final SoundEvent sound;
    private final float volume;
    private final float pitch;

    public SoundEffect(String soundName, float volume, float pitch) {
        // Look up the sound event in the BuiltInRegistries using Identifier
        this.sound = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(soundName));

        if (this.sound == null) {
            throw new IllegalArgumentException("Unknown sound: " + soundName);
        }

        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void apply(LivingEntity target) {
        // Play the sound at the target's location
        target.level().playSound(null, target.blockPosition(), sound, SoundSource.PLAYERS, volume, pitch);
    }
}
