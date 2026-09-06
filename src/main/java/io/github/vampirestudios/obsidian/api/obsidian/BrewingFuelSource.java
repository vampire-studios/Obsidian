package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

public class BrewingFuelSource {

	public Identifier item;
	public TagKey<Item> tag;

	public ResolvableInt uses;

	public ResolvableFloat speed_multiplier =
			ResolvableFloat.fromKey(ContextFloatProviders.BREWING_DEFAULT_SPEED_MULTIPLIER);
}