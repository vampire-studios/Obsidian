package io.github.vampirestudios.obsidian.api.obsidian.world;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.potion.EffectInstance;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mob to spawn, and what makes it different from the vanilla default.
 *
 * <p>Shared by a {@linkplain WorldEvent.Wave wave} and the {@code spawn_entity} action, so a mob is
 * described the same way wherever it is summoned from.
 */
public class MobSpec {

	/** The entity to spawn. */
	public Identifier entity;

	/** A name shown above the mob. */
	public NameInformation name;

	/** Whether the name is always visible rather than only when looked at. */
	@SerializedName("name_visible")
	public boolean nameVisible = false;

	/**
	 * What the mob is wearing and holding, keyed by slot: {@code head}, {@code chest}, {@code legs},
	 * {@code feet}, {@code mainhand}, {@code offhand}.
	 */
	public Map<String, Identifier> equipment = new HashMap<>();

	/**
	 * Attribute base values, keyed by attribute id — {@code minecraft:max_health},
	 * {@code minecraft:movement_speed}, {@code minecraft:attack_damage} and the rest.
	 */
	public Map<String, Double> attributes = new HashMap<>();

	/** Effects applied when the mob spawns. Use a long duration for one that should last the fight. */
	public List<EffectInstance> effects = new ArrayList<>();

	/**
	 * Whether the mob refuses to despawn. Worth setting for anything a fight depends on, since a mob
	 * that wanders out of range and vanishes would otherwise leave a stage that can never be cleared.
	 */
	public boolean persistent = true;

	/** Whether the mob gets its usual random armour, enchantments and variant for the difficulty. */
	@SerializedName("finalize_spawn")
	public boolean finalizeSpawn = true;

	public boolean hasEquipment() {
		return equipment != null && !equipment.isEmpty();
	}

	public boolean hasAttributes() {
		return attributes != null && !attributes.isEmpty();
	}
}
