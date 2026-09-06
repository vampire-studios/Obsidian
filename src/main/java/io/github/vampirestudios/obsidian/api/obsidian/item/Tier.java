package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class Tier {

	public int durability;
	@SerializedName("mining_speed")
	public float miningSpeed;
	@SerializedName("attack_damage")
	public float attackDamage;
	public int enchantability;

	@JsonProperty("repair_item")
	@SerializedName("repair_item")
	public Identifier repairItem;
	@JsonProperty("repair_tag")
	@SerializedName("repair_tag")
	public Identifier repairTag;

	@SerializedName("incorrect_blocks_for_drops")
	public Identifier incorrectBlocksForDrops;

	/** A concrete repair item overrides the tag supplied through the ToolMaterial. */
	public static Item.Properties applyRepairItem(Object material, Item.Properties properties) {
		Identifier tierId = switch (material) {
			case Identifier id -> id;
			case String value -> Identifier.tryParse(value);
			case null, default -> null;
		};
		if (tierId == null) return properties;

		Tier tier = ContentRegistries.TIERS.getValue(tierId);
		if (tier == null || tier.repairItem == null) return properties;

		Item repairItem = BuiltInRegistries.ITEM.getValue(tier.repairItem);
		return repairItem != null ? properties.repairable(repairItem) : properties;
	}

}
