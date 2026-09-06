package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

public class FuelSource {

	public String operation = "add";

	public Identifier item;
	public TagKey<Item> tag;

	public ResolvableInt burn_time;
	public ResolvableFloat speed_multiplier = ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER);

	public Operation getOperation() {
		if (operation.equals("remove")) {
			return Operation.REMOVE;
		}
		return Operation.ADD;
	}

	public enum Operation {
		ADD,
		REMOVE
	}
}