package io.github.vampirestudios.obsidian.api.crucible;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkillGroupManager {
	private Map<String, Long> groupCooldowns = new HashMap<>();
	private Set<String> unlockedSkills = new HashSet<>();

	public boolean canActivateSkill(SkillEntry skill) {
		// Check prerequisites
		for (String prerequisite : skill.getPrerequisites()) {
			if (!unlockedSkills.contains(prerequisite)) {
				return false;
			}
		}
		// Check cooldown
		String group = skill.getCooldownGroup();
		return System.currentTimeMillis() >= groupCooldowns.getOrDefault(group, 0L);
	}

	public void activateSkill(SkillEntry skillEntry, Skill skill) {
		String group = skillEntry.getCooldownGroup();
		int cooldown = skillEntry.getCooldown();
		groupCooldowns.put(group, System.currentTimeMillis() + cooldown * 1000L);
		unlockedSkills.add(skill.skillId); // Assume each skill has an ID or similar identifier
	}
}
