package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/** Passes while the caster's hunger is below a threshold. Only players have hunger. */
public class LowHungerCondition extends Condition {
	private final int minHunger;
	private final boolean applyToCaster;

	public LowHungerCondition(int minHunger, boolean applyToCaster) {
		super(List.of("lowhunger", "hunger", "hungerbelow"));
		this.minHunger = minHunger;
		this.applyToCaster = applyToCaster;
	}

	@Override
	public boolean evaluate(LivingEntity caster, LivingEntity target) {
		LivingEntity subject = applyToCaster ? caster : target;
		return subject instanceof Player player && player.getFoodData().getFoodLevel() < minHunger;
	}

	@Override
	public boolean applyToCaster() {
		return applyToCaster;
	}
}
