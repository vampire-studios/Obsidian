package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public record BundleSounds(SoundEvent insertSound, SoundEvent removeSound) {
	public static final Codec<BundleSounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("insert_sound").forGetter(BundleSounds::insertSound),
			BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("remove_sound").forGetter(BundleSounds::removeSound)
	).apply(instance, BundleSounds::new));
}
