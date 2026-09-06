package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public abstract class Condition {
	private final List<String> aliases;

	public Condition(List<String> aliases) {
		this.aliases = aliases;
	}

	public List<String> getAliases() {
		return aliases;
	}

	// Each condition will implement this to perform its specific check
	public abstract boolean evaluate(LivingEntity caster, LivingEntity target);

	public abstract boolean applyToCaster();  // Determines if this condition applies to the caster or target
}