package io.github.vampirestudios.obsidian.api.crucible;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillCooldownManager {
	private final Map<UUID, Map<String, Long>> playerSkillCooldowns = new HashMap<>();

	public boolean isOnCooldown(Player player, Skill skill) {
		Map<String, Long> cooldowns = playerSkillCooldowns.getOrDefault(player.getUUID(), new HashMap<>());
		Long cooldownEnd = cooldowns.get(skill.skillId);
		return cooldownEnd != null && System.currentTimeMillis() < cooldownEnd;
	}

	public void setCooldown(Player player, Skill skill, long cooldownMillis) {
		playerSkillCooldowns.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
				.put(skill.skillId, System.currentTimeMillis() + cooldownMillis);
	}
}
