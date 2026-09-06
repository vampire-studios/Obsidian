package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public abstract class Effect {
	private Identifier id;

	public void setId(Identifier id) {
		this.id = id;
	}

	public Identifier getId() {
		return id;
	}

	public abstract void apply(LivingEntity target);
}
