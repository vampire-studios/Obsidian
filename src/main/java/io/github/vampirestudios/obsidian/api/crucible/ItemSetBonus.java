package io.github.vampirestudios.obsidian.api.crucible;

import java.util.List;
import java.util.Map;

public class ItemSetBonus {
	public List<String> Skills;
	public Map<String, Double> Attributes;
	/** Compiled skills — populated at load time. */
	public List<Skill> internalSkills;
}
