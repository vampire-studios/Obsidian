package io.github.vampirestudios.obsidian.addon_modules;

import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.FuelSource;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CookingFuel;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class FuelSources implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		FuelSource fuelSource = AddonFormats.read(addon, file, FuelSource.class);
		JsonObject json = AddonFormats.readObject(addon, file);

		try {
			if (fuelSource == null) return;

			Predicate<Item> predicate;

			if (fuelSource.item != null) {
				Item item = BuiltInRegistries.ITEM.getValue(fuelSource.item);
				predicate = candidate -> candidate == item;
			} else {
				predicate = item -> item.builtInRegistryHolder().is(fuelSource.tag);
			}

			DefaultItemComponentEvents.MODIFY.register(context ->
					context.modify(predicate, (builder, item) -> {
						if (json.entrySet().isEmpty() || fuelSource.burn_time == null) {
							builder.set(DataComponents.COOKING_FUEL, null);
							return;
						}

						builder.set(DataComponents.COOKING_FUEL, new CookingFuel(fuelSource.burn_time, fuelSource.speed_multiplier));
					})
			);

			register(
					ContentRegistries.FUEL_SOURCES,
					"fuel_source",
					Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file)),
					fuelSource
			);
		} catch (Exception e) {
			failedRegistering("fuel_source", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "fuel_source/cooking";
	}
}