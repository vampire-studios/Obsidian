package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class EntityRuntimeRegistries {
	public interface EventActionHandler {
		void execute(EntityImpl entity, Event.Action action, int depth);
	}

	public interface GoalBackend {
		void apply(EntityImpl entity, Entity.StructuredAI ai, Map<String, Component> activeComponents);
	}

	public static final Map<String, EventActionHandler> EVENT_ACTIONS = new HashMap<>();
	public static final Map<String, GoalBackend> AI_BACKENDS = new HashMap<>();

	private static boolean initialized;

	private EntityRuntimeRegistries() {
	}

	public static void bootstrap() {
		if (initialized) return;
		initialized = true;

		registerEventAction("set_property", (entity, action, depth) -> entity.applySetProperty(action));
		registerEventAction("trigger_event", (entity, action, depth) -> {
			if (action.event != null) entity.triggerEvent(action.event, depth + 1);
		});
		registerEventAction("sequence", (entity, action, depth) -> {
			for (Event.Action nested : action.actions) {
				entity.executeAction(nested, depth + 1);
			}
		});
		registerEventAction("random_choice", (entity, action, depth) -> {
			if (action.choices.isEmpty()) return;

			int total = action.choices.stream().mapToInt(choice -> Math.max(1, choice.weight)).sum();
			int roll = new Random().nextInt(total);
			int cursor = 0;
			for (Event.WeightedChoice choice : action.choices) {
				cursor += Math.max(1, choice.weight);
				if (roll >= cursor) continue;

				if (choice.action != null) {
					entity.executeAction(choice.action, depth + 1);
				} else {
					for (Event.Action nested : choice.actions) {
						entity.executeAction(nested, depth + 1);
					}
				}
				return;
			}
		});
		registerEventAction("if", (entity, action, depth) -> {
			boolean matches = action.predicate == null && action.condition == null;
			if (action.predicate != null) {
				matches = EntityStateResolver.evaluatePredicate(entity.getEntityDefinition(), action.predicate, entity.getProperties());
			} else if (action.condition != null) {
				matches = EntityStateResolver.evaluatePredicate(entity.getEntityDefinition(), action.condition, entity.getProperties(), new java.util.HashSet<>());
			}
			if (!matches) return;

			for (Event.Action nested : action.then) {
				entity.executeAction(nested, depth + 1);
			}
		});
		registerEventAction("start_timer", (entity, action, depth) -> entity.startTimer(action.timer, action.duration_ticks));
		registerEventAction("stop_timer", (entity, action, depth) -> entity.stopTimer(action.timer));

		registerAiBackend("goal_selector", (entity, ai, activeComponents) -> entity.applyGoalSelector(ai.goal_selector, activeComponents));
		registerAiBackend("brain", (entity, ai, activeComponents) -> {
			// Brain backend is reserved for future memory/sensor/activity runtime hooks.
		});
	}

	public static void registerEventAction(String id, EventActionHandler handler) {
		EVENT_ACTIONS.put(id, handler);
	}

	public static void registerAiBackend(String id, GoalBackend backend) {
		AI_BACKENDS.put(id, backend);
	}
}
