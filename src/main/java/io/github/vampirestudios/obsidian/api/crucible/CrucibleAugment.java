package io.github.vampirestudios.obsidian.api.crucible;

import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

/** Runtime representation of an augment item (from CrucibleItem.AugmentationDef). */
public class CrucibleAugment {
	public Identifier id;
	/** Raw display name of the parent item, used as fallback tooltip. */
	public String displayName;
	public String Type;
	public String Tooltip;
	public String Icon;
	public List<String> Conditions;
	public Map<String, Double> Attributes;
	public List<String> Skills;
	public List<Skill> internalSkills;

	public CrucibleAugment() {
	}

	public CrucibleAugment(Identifier id, String displayName, String type, String tooltip, String icon,
	                       List<String> conditions, Map<String, Double> attributes,
	                       List<String> skills, List<Skill> internalSkills) {
		this.id = id;
		this.displayName = displayName;
		this.Type = type;
		this.Tooltip = tooltip;
		this.Icon = icon;
		this.Conditions = conditions;
		this.Attributes = attributes;
		this.Skills = skills;
		this.internalSkills = internalSkills;
	}

	public String getEffectiveTooltip() {
		return (Tooltip != null && !Tooltip.isEmpty()) ? Tooltip : displayName;
	}
}
