package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.Direction;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The optional half of a block conversion, shared by chisel mappings and convertible blocks. Every field
 * maps onto vanilla's {@code BlockTransformData}, so both formats expose the same surface and gain any
 * future option in one place.
 */
public class BlockTransformOptions {

	private static final Logger LOGGER = LogManager.getLogger();

	/** Particle shown on conversion: {@code none}, {@code scrape}, {@code wax_on} or {@code wax_off}. */
	public String particle;

	/** Faces the block may not be converted from, e.g. {@code ["down"]}. */
	@SerializedName("disallowed_faces")
	public List<String> disallowedFaces;

	/** Loot table dropped by the conversion. */
	public String loot;

	/** Where drops appear: {@code clicked_face} or {@code from_middle}. */
	@SerializedName("drop_strategy")
	public String dropStrategy;

	/** Whether the new block re-evaluates its state from its neighbours. */
	@SerializedName("update_from_neighbors")
	public Boolean updateFromNeighbors;

	/** {@code single_block} or {@code copper_chest}. */
	@SerializedName("transform_type")
	public String transformType;

	/** Whether one item is consumed per conversion. */
	@SerializedName("consume_on_use")
	public Boolean consumeOnUse;

	/** Durability removed from the item per conversion. */
	@SerializedName("item_damage_per_use")
	public Integer itemDamagePerUse;

	public void applyTo(BlockTransformer.BlockTransformData.Builder builder, Object context) {
		if (particle != null) {
			BlockTransformer.TransformParticle value = parse(BlockTransformer.TransformParticle.class, particle, "particle", context);
			if (value != null) builder.particle(value);
		}

		if (disallowedFaces != null && !disallowedFaces.isEmpty()) {
			List<Direction> faces = new ArrayList<>();
			for (String name : disallowedFaces) {
				Direction direction = Direction.byName(name);
				if (direction == null) {
					LOGGER.warn("[Obsidian] Unknown direction \"{}\" in disallowed_faces for {}.", name, context);
					continue;
				}
				faces.add(direction);
			}
			if (!faces.isEmpty()) builder.disallowedFaces(faces);
		}

		if (loot != null) {
			Identifier lootId = Identifier.tryParse(loot);
			if (lootId == null) {
				LOGGER.warn("[Obsidian] Invalid loot table \"{}\" for {}.", loot, context);
			} else {
				builder.loot(ResourceKey.create(Registries.LOOT_TABLE, lootId));
			}
		}

		if (dropStrategy != null) {
			BlockTransformer.DropStrategy value = parse(BlockTransformer.DropStrategy.class, dropStrategy, "drop_strategy", context);
			if (value != null) builder.dropStrategy(value);
		}

		if (transformType != null) {
			BlockTransformer.TransformType value = parse(BlockTransformer.TransformType.class, transformType, "transform_type", context);
			if (value != null) builder.transformType(value);
		}

		if (updateFromNeighbors != null) builder.updateFromNeighbors(updateFromNeighbors);
		if (consumeOnUse != null) builder.consumeOnUse(consumeOnUse);
		if (itemDamagePerUse != null) builder.itemDamagePerUse(itemDamagePerUse);
	}

	private static <T extends Enum<T>> T parse(Class<T> type, String value, String field, Object context) {
		try {
			return Enum.valueOf(type, value.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			LOGGER.warn("[Obsidian] Unknown {} \"{}\" for {}.", field, value, context);
			return null;
		}
	}
}
