package io.github.vampirestudios.obsidian.api.obsidian.entity;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {

	@SerializedName(value = "description", alternate = {"information"})
	public Information description;

	public Map<String, EntityProperty> properties = new HashMap<>();
	public Map<String, Component> components = new HashMap<>();
	public List<ComponentTwo> components_two = new ArrayList<>();

	@SerializedName("component_sets")
	public Map<String, ComponentGroup> component_sets = new HashMap<>();

	@Deprecated
	public Map<String, ComponentGroup> component_groups = new HashMap<>();

	public List<StateRule> states = new ArrayList<>();
	public Map<String, Event.PredicateDefinition> predicates = new HashMap<>();
	public Map<String, TimerDefinition> timers = new HashMap<>();
	public Map<String, Event> events = new HashMap<>();
	public Map<String, Double> attributes = new HashMap<>();
	public List<InteractionEntry> interactions = new ArrayList<>();
	public StructuredAI ai;

	public List<Identifier> animations;

	@SerializedName("shadow_size")
	public float shadowSize = 1.0F;

	public EntityComponents entity_components;

	public Information getDescription() {
		return description;
	}

	public Map<String, ComponentGroup> getComponentSets() {
		if (!component_sets.isEmpty()) return component_sets;
		return component_groups;
	}

	public static class EntityProperty {
		public String type = "string";

		@SerializedName(value = "default", alternate = {"default_value"})
		public Object defaultValue;

		@SerializedName("enum_values")
		public List<String> enumValues = new ArrayList<>();

		@SerializedName("client_sync")
		public boolean clientSync;
	}

	public static class StateRule {
		public String name;
		public String property;
		public com.google.gson.JsonElement equals;
		public List<com.google.gson.JsonElement> in = new ArrayList<>();
		public String predicate;
		public List<String> apply = new ArrayList<>();
	}

	public static class TimerDefinition {
		public int duration_ticks = 20;
		public String on_complete_event;
		public boolean loop;
	}

	public static class InteractionEntry {
		public String id;
		public String display_text;
		public String predicate;
		public Event.PredicateDefinition condition;
		public List<Event.Action> actions = new ArrayList<>();
		public Identifier sound;
		public Integer item_damage;
		public String equipment_slot;
		public Identifier equipment_item;
	}

	public static class StructuredAI extends Component {
		public GoalSelector goal_selector = new GoalSelector();
		public Brain brain = new Brain();
	}

	public static class GoalSelector {
		public String backend = "goal_selector";
		public List<GoalEntry> goals = new ArrayList<>();
		public List<GoalEntry> targets = new ArrayList<>();
	}

	public static class GoalEntry {
		public String id;
		public Integer priority;
		public String component;
	}

	public static class Brain {
		public String backend = "brain";
		public List<String> sensors = new ArrayList<>();
		public List<String> activities = new ArrayList<>();
		public List<String> memories = new ArrayList<>();
	}
}
