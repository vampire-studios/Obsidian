package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Event;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.BreathableComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.movement.BasicMovementComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import java.util.*;

public class EntityImpl extends PathfinderMob {
	private final Entity entity;
	private final float health;
	private final Map<String, Component> baseComponents;
	private Map<String, Component> activeComponents = new HashMap<>();
	private final Map<String, Object> properties = new HashMap<>();
	private final Map<String, Integer> activeTimers = new HashMap<>();
	private final BreathableComponent breathableComponent;
	private boolean goalsInitialized;
	public final Map<AnimationState, Identifier> animationStates = new HashMap<>();

	public EntityImpl(EntityType<EntityImpl> type, Level world, Entity entity, float health, BreathableComponent breathableComponent) {
		super(type, world);
		this.entity = entity;
		this.health = health;
		this.baseComponents = entity.components != null ? entity.components : new HashMap<>();
		this.breathableComponent = breathableComponent;
		this.properties.putAll(EntityStateResolver.createDefaultProperties(entity));
		this.activeComponents = EntityStateResolver.resolveActiveComponents(entity, properties);
		EntityRuntimeRegistries.bootstrap();
		ensureGoalsInitialized();

		if (this.entity.animations != null)
			this.entity.animations.forEach(identifier -> animationStates.put(new AnimationState(), identifier));
	}

	public Entity getEntityDefinition() {
		return entity;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public float getWalkTargetValue(BlockPos pos, LevelReader world) {
		return super.getWalkTargetValue(pos, world);
	}

	@Override
	public boolean hasCustomName() {
		return activeComponents.containsKey("minecraft:namable");
	}

	@Override
	public boolean isCustomNameVisible() {
		return activeComponents.containsKey("minecraft:namable");
	}

	@Override
	public float getHealth() {
		return health;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		ensureGoalsInitialized();
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) return;
		tickTimers();
	}

	public void triggerEvent(String eventId, int depth) {
		if (eventId == null || eventId.isBlank()) return;
		if (depth > 24) return;
		if (entity.events == null) return;
		Event event = entity.events.get(eventId);
		if (event == null || event.actions == null) return;
		for (Event.Action action : event.actions) {
			executeAction(action, depth + 1);
		}
	}

	public void executeAction(Event.Action action, int depth) {
		if (action == null || action.type == null || action.type.isBlank()) return;
		var handler = EntityRuntimeRegistries.EVENT_ACTIONS.get(action.type);
		if (handler == null) return;
		handler.execute(this, action, depth);
	}

	public InteractionResult handleInteraction(Player player, InteractionHand hand) {
		if (player == null || hand == null) return InteractionResult.PASS;
		if (entity.interactions == null || entity.interactions.isEmpty()) return InteractionResult.PASS;

		for (Entity.InteractionEntry interaction : entity.interactions) {
			if (interaction == null) continue;

			boolean predicateMatches = interaction.predicate == null || interaction.predicate.isBlank()
					|| EntityStateResolver.evaluatePredicate(entity, interaction.predicate, properties);
			if (!predicateMatches) continue;

			boolean conditionMatches = interaction.condition == null
					|| EntityStateResolver.evaluatePredicate(entity, interaction.condition, properties, new HashSet<>());
			if (!conditionMatches) continue;

			if (interaction.actions != null) {
				for (Event.Action action : interaction.actions) {
					executeAction(action, 0);
				}
			}

			if (interaction.sound != null) {
				var soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(interaction.sound);
				if (soundEvent != null) {
					level().playSound(
							null,
							blockPosition(),
							soundEvent,
							/*soundSource()*/SoundSource.AMBIENT,
							1.0F,
							1.0F
					);
				}
			}

			if (interaction.item_damage != null && interaction.item_damage > 0 && !player.getAbilities().instabuild) {
				ItemStack held = player.getItemInHand(hand);
				held.hurtAndBreak(interaction.item_damage, player, hand.asEquipmentSlot());
			}

			if (interaction.equipment_slot != null && interaction.equipment_item != null) {
				EquipmentSlot slot = parseEquipmentSlot(interaction.equipment_slot);
				if (slot != null) {
					var item = BuiltInRegistries.ITEM.getValue(interaction.equipment_item);
					if (item != null) {
						setItemSlot(slot, new ItemStack(item));
					}
				}
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	public void applySetProperty(Event.Action action) {
		if (action.property == null || action.property.isBlank()) return;
		Entity.EntityProperty definition = entity.properties != null ? entity.properties.get(action.property) : null;
		Object value = EntityStateResolver.coercePropertyValue(definition, action.value);
		properties.put(action.property, value);
		resolveStateAndGoals();
	}

	public void startTimer(String timerName, Integer durationOverride) {
		if (timerName == null || timerName.isBlank()) return;
		Entity.TimerDefinition timerDefinition = entity.timers != null ? entity.timers.get(timerName) : null;
		int duration = durationOverride != null ? durationOverride : (timerDefinition != null ? timerDefinition.duration_ticks : 20);
		activeTimers.put(timerName, Math.max(1, duration));
	}

	public void stopTimer(String timerName) {
		if (timerName == null || timerName.isBlank()) return;
		activeTimers.remove(timerName);
	}

	public void applyGoalSelector(Entity.GoalSelector selector, Map<String, Component> components) {
		if (selector == null) return;

		for (Entity.GoalEntry entry : selector.goals) {
			if (entry == null) continue;
			String componentId = entry.component != null ? entry.component : entry.id;
			if (componentId == null || componentId.isBlank()) continue;
			applyGoalForComponent(componentId, components.get(componentId), entry.priority);
		}

		for (Entity.GoalEntry entry : selector.targets) {
			if (entry == null) continue;
			String componentId = entry.component != null ? entry.component : entry.id;
			if (componentId == null || componentId.isBlank()) continue;
			applyGoalForComponent(componentId, components.get(componentId), entry.priority);
		}
	}

	private void resolveStateAndGoals() {
		activeComponents = EntityStateResolver.resolveActiveComponents(entity, properties);
		if (entity.ai != null) {
			activeComponents.putIfAbsent("minecraft:ai", entity.ai);
		}
		// Goals are currently initialized once for stability. A full dynamic goal hot-swap
		// path can be added later with explicit remove/rebuild support.
	}

	private void tickTimers() {
		if (activeTimers.isEmpty()) return;
		List<String> completed = new ArrayList<>();

		for (Map.Entry<String, Integer> entry : activeTimers.entrySet()) {
			int ticksLeft = entry.getValue() - 1;
			if (ticksLeft <= 0) {
				completed.add(entry.getKey());
			} else {
				entry.setValue(ticksLeft);
			}
		}

		for (String timerName : completed) {
			activeTimers.remove(timerName);
			Entity.TimerDefinition timerDefinition = entity.timers != null ? entity.timers.get(timerName) : null;
			if (timerDefinition != null) {
				if (timerDefinition.loop) {
					activeTimers.put(timerName, Math.max(1, timerDefinition.duration_ticks));
				}
				if (timerDefinition.on_complete_event != null) {
					triggerEvent(timerDefinition.on_complete_event, 0);
				}
			}
		}
	}

	private void rebuildGoals() {
		if (entity == null) return;

		Map<String, Component> resolved = new HashMap<>(baseComponents);
		resolved.putAll(activeComponents);

		Component aiComponent = resolved.get("minecraft:ai");
		if (aiComponent instanceof Entity.StructuredAI structuredAI) {
			var goalBackend = EntityRuntimeRegistries.AI_BACKENDS.get(structuredAI.goal_selector.backend);
			if (goalBackend != null) {
				goalBackend.apply(this, structuredAI, resolved);
			}
			var brainBackend = EntityRuntimeRegistries.AI_BACKENDS.get(structuredAI.brain.backend);
			if (brainBackend != null) {
				brainBackend.apply(this, structuredAI, resolved);
			}
		}

		applyGoalForComponent("minecraft:movement.basic", resolved.get("minecraft:movement.basic"), null);
		applyGoalForComponent("minecraft:behavior.random_stroll", resolved.get("minecraft:behavior.random_stroll"), null);
		applyGoalForComponent("minecraft:behavior.panic", resolved.get("minecraft:behavior.panic"), null);
		applyGoalForComponent("minecraft:behavior.tempt", resolved.get("minecraft:behavior.tempt"), null);
		applyGoalForComponent("minecraft:behavior.random_look_around", resolved.get("minecraft:behavior.random_look_around"), null);
		applyGoalForComponent("minecraft:behavior.look_at_player", resolved.get("minecraft:behavior.look_at_player"), null);
	}

	private void ensureGoalsInitialized() {
		if (goalsInitialized) return;
		if (entity == null) return;
		goalsInitialized = true;
		rebuildGoals();
	}

	private void applyGoalForComponent(String componentId, Component component, Integer priorityOverride) {
		if (component == null || componentId == null) return;

		if ("minecraft:movement.basic".equals(componentId) && component instanceof BasicMovementComponent) {
			int priority = priorityOverride != null ? priorityOverride : 1;
			this.goalSelector.addGoal(priority, new WaterAvoidingRandomStrollGoal(this, 1.0D));
			return;
		}

		if ("minecraft:behavior.panic".equals(componentId) && component instanceof PanicBehaviourComponent panicBehaviourComponent) {
			int priority = priorityOverride != null ? priorityOverride : panicBehaviourComponent.priority;
			this.goalSelector.addGoal(priority, new PanicGoal(this, panicBehaviourComponent.speed_multiplier));
			return;
		}

		if ("minecraft:behavior.random_stroll".equals(componentId) && component instanceof RandomStrollBehaviourComponent randomStrollBehaviourComponent) {
			int priority = priorityOverride != null ? priorityOverride : randomStrollBehaviourComponent.priority;
			double speed = randomStrollBehaviourComponent.speed_multiplier > 0.0F ? randomStrollBehaviourComponent.speed_multiplier : 1.0D;
			this.goalSelector.addGoal(priority, new WaterAvoidingRandomStrollGoal(this, speed));
			return;
		}

		if ("minecraft:behavior.tempt".equals(componentId) && component instanceof TemptBehaviourComponent temptBehaviourComponent) {
			int priority = priorityOverride != null ? priorityOverride : temptBehaviourComponent.priority;
			List<ItemStack> temptItems = new ArrayList<>();
			temptBehaviourComponent.items.forEach(item -> temptItems.add(new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.tryParse(item)))));
			this.goalSelector.addGoal(priority, new TemptGoal(this, temptBehaviourComponent.speed_multiplier, Ingredient.of(temptItems.stream().map(ItemStack::getItem)), temptBehaviourComponent.can_be_scared));
			return;
		}

		if ("minecraft:behavior.random_look_around".equals(componentId) && component instanceof RandomLookAroundBehaviourComponent randomLookAroundBehaviourComponent) {
			int priority = priorityOverride != null ? priorityOverride : randomLookAroundBehaviourComponent.priority;
			this.goalSelector.addGoal(priority, new RandomLookAroundGoal(this));
			return;
		}

		if ("minecraft:behavior.look_at_player".equals(componentId) && component instanceof LookAtPlayerBehaviourComponent lookAtPlayerBehaviourComponent) {
			int priority = priorityOverride != null ? priorityOverride : lookAtPlayerBehaviourComponent.priority;
			this.goalSelector.addGoal(priority, new LookAtPlayerGoal(this, Player.class, lookAtPlayerBehaviourComponent.look_distance, lookAtPlayerBehaviourComponent.probability));
		}
	}

	private EquipmentSlot parseEquipmentSlot(String slotName) {
		if (slotName == null || slotName.isBlank()) return null;
		try {
			return EquipmentSlot.valueOf(slotName.toUpperCase(java.util.Locale.ROOT));
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

}
