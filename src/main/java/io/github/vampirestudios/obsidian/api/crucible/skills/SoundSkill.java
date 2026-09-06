package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Locale;

public class SoundSkill extends Skill {
	private final String sound;
	private final String soundSource;
	private final float pitch;
	private final float volume;

	public SoundSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, String sound, String soundSource, float pitch, float volume) {
		super(skillId, target, trigger);
		this.sound = sound;
		this.soundSource = soundSource;
		this.pitch = pitch;
		this.volume = volume;
	}

	@Override
	public void applyEffect(LivingEntity caster, LivingEntity target) {
		BlockPos pos = target.blockPosition(); // Get the block position of the target
		target.level().playSound(null, pos, BuiltInRegistries.SOUND_EVENT.getValueOrThrow(ResourceKey.create(Registries.SOUND_EVENT, Identifier.parse(sound))), SoundSource.valueOf(soundSource.toUpperCase(Locale.ROOT)), volume, pitch);
	}
}
