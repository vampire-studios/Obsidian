package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.potion.EffectInstance;
import io.github.vampirestudios.obsidian.api.obsidian.world.MobSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

/**
 * Builds a mob from a {@link MobSpec}.
 *
 * <p>Shared by waves and the {@code spawn_entity} action: both used to produce a bare vanilla entity,
 * which made a siege of identical stock zombies the only thing either could express.
 */
public final class MobSpawner {

	private MobSpawner() {
	}

	/**
	 * Spawns one mob at a position.
	 *
	 * @return the spawned entity, or null when the entity is unknown or the level refused it
	 */
	public static @Nullable Entity spawn(ServerLevel level, BlockPos at, MobSpec spec) {
		if (spec.entity == null) return null;

		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(spec.entity);
		if (type == null) {
			Obsidian.LOGGER.warn("Cannot spawn unknown entity {}.", spec.entity);
			return null;
		}
		return spawn(level, at, type, spec);
	}

	/** As {@link #spawn(ServerLevel, BlockPos, MobSpec)}, for a type already resolved. */
	public static @Nullable Entity spawn(ServerLevel level, BlockPos at, EntityType<?> type, MobSpec spec) {
		Entity entity = type.create(level, EntitySpawnReason.EVENT);
		if (entity == null) return null;

		entity.moveOrInterpolateTo(Vec3.atBottomCenterOf(at), level.getRandom().nextFloat() * 360.0F, 0.0F);

		// Vanilla's own randomisation runs first, so anything the spec sets wins over it.
		if (spec.finalizeSpawn && entity instanceof Mob mob) {
			mob.finalizeSpawn(level, level.getCurrentDifficultyAt(at), EntitySpawnReason.EVENT, null);
		}

		customise(level, entity, spec);

		return level.addFreshEntity(entity) ? entity : null;
	}

	/** Applies a spec to an entity that already exists. */
	public static void customise(ServerLevel level, Entity entity, MobSpec spec) {
		if (spec.name != null) {
			entity.setCustomName(spec.name.getName());
			entity.setCustomNameVisible(spec.nameVisible);
		}

		if (entity instanceof Mob mob && spec.persistent) mob.setPersistenceRequired();

		if (!(entity instanceof LivingEntity living)) return;

		if (spec.hasAttributes()) applyAttributes(living, spec.attributes);
		if (spec.hasEquipment()) applyEquipment(living, spec.equipment);

		for (EffectInstance declared : spec.effects) {
			MobEffectInstance effect = declared == null ? null : declared.toInstance();
			if (effect != null) living.addEffect(effect);
		}
	}

	/**
	 * Sets attribute base values. Health is raised to the new maximum, since a mob given more
	 * {@code max_health} would otherwise spawn on its old value and look already wounded.
	 */
	private static void applyAttributes(LivingEntity living, Map<String, Double> attributes) {
		boolean healthChanged = false;

		for (Map.Entry<String, Double> entry : attributes.entrySet()) {
			Identifier id = Identifier.tryParse(entry.getKey());
			if (id == null || entry.getValue() == null) continue;

			Holder<Attribute> attribute = BuiltInRegistries.ATTRIBUTE.get(id).map(holder -> (Holder<Attribute>) holder).orElse(null);
			if (attribute == null) {
				Obsidian.LOGGER.warn("Unknown attribute {} on a spawned mob.", entry.getKey());
				continue;
			}

			AttributeInstance instance = living.getAttribute(attribute);
			if (instance == null) continue;

			instance.setBaseValue(entry.getValue());
			if (id.equals(Identifier.withDefaultNamespace("max_health"))) healthChanged = true;
		}

		if (healthChanged) living.setHealth(living.getMaxHealth());
	}

	private static void applyEquipment(LivingEntity living, Map<String, Identifier> equipment) {
		for (Map.Entry<String, Identifier> entry : equipment.entrySet()) {
			EquipmentSlot slot = slotOf(entry.getKey());
			if (slot == null) {
				Obsidian.LOGGER.warn("Unknown equipment slot \"{}\" on a spawned mob.", entry.getKey());
				continue;
			}

			Item item = entry.getValue() == null ? null : BuiltInRegistries.ITEM.getValue(entry.getValue());
			if (item == null) continue;

			living.setItemSlot(slot, new ItemStack(item));
		}
	}

	/** Accepts the slot names a pack would write, which are not quite the enum's. */
	private static @Nullable EquipmentSlot slotOf(String name) {
		if (name == null) return null;
		return switch (name.toLowerCase(Locale.ROOT)) {
			case "head", "helmet" -> EquipmentSlot.HEAD;
			case "chest", "chestplate" -> EquipmentSlot.CHEST;
			case "legs", "leggings" -> EquipmentSlot.LEGS;
			case "feet", "boots" -> EquipmentSlot.FEET;
			case "mainhand", "main_hand", "hand" -> EquipmentSlot.MAINHAND;
			case "offhand", "off_hand" -> EquipmentSlot.OFFHAND;
			case "body" -> EquipmentSlot.BODY;
			default -> null;
		};
	}
}
