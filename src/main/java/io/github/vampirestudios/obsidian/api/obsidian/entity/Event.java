package io.github.vampirestudios.obsidian.api.obsidian.entity;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class Event {
	public List<Action> actions = new ArrayList<>();

	public static class Action {
		public String type;
		public String property;
		public JsonElement value;
		public String event;
		public String timer;
		public Integer duration_ticks;
		public String predicate;
		public PredicateDefinition condition;
		public List<Action> then = new ArrayList<>();
		public List<Action> actions = new ArrayList<>();
		public List<WeightedChoice> choices = new ArrayList<>();
	}

	public static class WeightedChoice {
		public int weight = 1;
		public Action action;
		public List<Action> actions = new ArrayList<>();
	}

	public static class PredicateDefinition {
		public String ref;
		public String property;
		public JsonElement equals;
		public List<JsonElement> in = new ArrayList<>();
		public Boolean value;
		public List<PredicateDefinition> all = new ArrayList<>();
		public List<PredicateDefinition> any = new ArrayList<>();
		public PredicateDefinition not;
	}
}
