package io.github.vampirestudios.obsidian.api.crucible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillEntry {
	protected String id;

	private List<String> conditionLines;
	private List<String> effectLines;
	private Map<String, String> skillParameters;
	private String target;
	private Map<String, String> targetParameters = new HashMap<>();
	private String eventName;
	private String eventParameter;
	private String cooldownGroup;
	private int cooldown;
	private List<String> prerequisites;
	private List<SkillEntry> subSkills;
	private List<Buff> buffs;

	// Add fields for conditions
	private List<String> conditions;
	private List<String> targetConditions;
	private int repeat = 1; // Default to 1, meaning it runs once if not specified
	private int repeatInterval = 0; // Default to 0, meaning no interval if not specified

	public SkillEntry() {
		this.skillParameters = new HashMap<>();
		this.prerequisites = new ArrayList<>();
		this.buffs = new ArrayList<>();
		this.conditions = new ArrayList<>();
		this.targetConditions = new ArrayList<>();
		this.conditionLines = new ArrayList<>();
		this.effectLines = new ArrayList<>();
	}

	// Getters and setters
	public Map<String, String> getSkillParameters() {
		return skillParameters;
	}

	public void setSkillParameters(Map<String, String> skillParameters) {
		this.skillParameters = skillParameters;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventParameter() {
		return eventParameter;
	}

	public void setEventParameter(String eventParameter) {
		this.eventParameter = eventParameter;
	}

	public List<SkillEntry> getSubSkills() {
		return subSkills;
	}

	public void setSubSkills(List<SkillEntry> subSkills) {
		this.subSkills = subSkills;
	}

	public String getCooldownGroup() {
		return cooldownGroup;
	}

	public void setCooldownGroup(String cooldownGroup) {
		this.cooldownGroup = cooldownGroup;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public List<String> getPrerequisites() {
		return prerequisites;
	}

	public void setPrerequisites(List<String> prerequisites) {
		this.prerequisites = prerequisites;
	}

	public List<Buff> getBuffs() {
		return buffs;
	}

	public void setBuffs(List<Buff> buffs) {
		this.buffs = buffs;
	}

	public void setTargetParameters(Map<String, String> targetParameters) {
		this.targetParameters = targetParameters;
	}

	public Map<String, String> getTargetParameters() {
		return targetParameters;
	}

	// Getters and setters for conditions
	public List<String> getConditions() {
		return conditions;
	}

	public void setConditions(List<String> conditions) {
		this.conditions = conditions;
	}

	public List<String> getTargetConditions() {
		return targetConditions;
	}

	public void setTargetConditions(List<String> targetConditions) {
		this.targetConditions = targetConditions;
	}

	public List<String> getConditionLines() {
		return conditionLines;
	}

	public List<String> getEffectLines() {
		return effectLines;
	}

	// Getters and Setters for repeat and repeatInterval
	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public int getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(int repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	@Override
	public String toString() {
		return "SkillEntry{skillParameters=" + skillParameters + ", target='" + target + "', eventName='" + eventName + "', eventParameter='" + eventParameter + "', subSkills=" + subSkills + "}";
	}

	public class Buff {
		private String type;
		private int maxStacks;
		private int duration; // in seconds
		private int currentStacks;
		private long expiryTime;

		public Buff(String type, int maxStacks, int duration) {
			this.type = type;
			this.maxStacks = maxStacks;
			this.duration = duration;
			this.currentStacks = 1;
			this.expiryTime = System.currentTimeMillis() + duration * 1000L;
		}

		public void applyStack() {
			if (currentStacks < maxStacks) {
				currentStacks++;
			}
			refreshDuration();
		}

		public boolean isExpired() {
			return System.currentTimeMillis() > expiryTime;
		}

		public void refreshDuration() {
			this.expiryTime = System.currentTimeMillis() + duration * 1000L;
		}

		public String getType() {
			return type;
		}

		public int getMaxStacks() {
			return maxStacks;
		}

		public int getDuration() {
			return duration;
		}

		public int getCurrentStacks() {
			return currentStacks;
		}

		public long getExpiryTime() {
			return expiryTime;
		}
	}

}