package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.EntityVariableManager;
import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Reads a variable and runs the case matching its value, or the default when nothing matches.
 *
 * <p>The previous version held a single condition and ran whichever case the map happened to iterate
 * first, ignoring the case names entirely — an if/else wearing a switch's clothes. A switch needs a
 * <em>value</em> to switch on, so it reads one from the entity's variables. Values are compared as
 * text, case-insensitively, because that is what {@code setvariable{}} produces.
 */
public class SwitchSkill extends Skill {

	private final String variableName;

	/** Whether the variable is read off the caster or the target. */
	private final boolean onCaster;

	private final Map<String, Skill> cases;
	private final Skill defaultCase;

	public SwitchSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger,
	                   String variableName, boolean onCaster, Map<String, Skill> cases, Skill defaultCase) {
		super(skillId, target, trigger);
		this.variableName = variableName;
		this.onCaster = onCaster;
		this.cases = Objects.requireNonNullElse(cases, Map.of());
		this.defaultCase = defaultCase;
	}

	@Override
	public void applyEffect(LivingEntity caster, LivingEntity target) {
		LivingEntity subject = onCaster ? caster : target;

		Skill chosen = select(subject);
		if (chosen != null) chosen.runSkill(caster, target);
	}

	/** The case whose name equals the variable's current value, or the default. */
	private Skill select(LivingEntity subject) {
		if (subject == null) return defaultCase;

		Object value = EntityVariableManager.getEntityVariable(subject, variableName);
		if (value == null) return defaultCase;

		String key = String.valueOf(value).trim().toLowerCase(Locale.ROOT);
		return cases.getOrDefault(key, defaultCase);
	}

	public Map<String, Skill> cases() {
		return cases;
	}
}
