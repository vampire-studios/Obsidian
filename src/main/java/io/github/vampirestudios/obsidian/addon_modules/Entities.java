package io.github.vampirestudios.obsidian.addon_modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.ComponentGroup;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.BreathableComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.CollisionBoxComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.HealthComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.MovementComponent;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityStateResolver;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.EntityRegistryBuilder;
import io.github.vampirestudios.obsidian.utils.EntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Entities implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		JsonObject entityJson = AddonFormats.readObject(addon, file);
		Entity entity = BaseGson.GSON.fromJson(entityJson, Entity.class);
		try {
			if (entity == null) return;

			entity.description = entity.getDescription();
			if (entity.description == null) {
				throw new JsonParseException("Missing 'description' section in entity json.");
			}

			Identifier entityId = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			entity.description.id = entityId;

			String baseColor = "000000";
			String overlayColor = "ffffff";
			if (entity.description.spawn_egg != null) {
				if (entity.description.spawn_egg.base_color != null) {
					baseColor = entity.description.spawn_egg.base_color.replace("#", "").replace("0x", "");
				}
				if (entity.description.spawn_egg.overlay_color != null) {
					overlayColor = entity.description.spawn_egg.overlay_color.replace("#", "").replace("0x", "");
				}
			}

			entity.components = parseComponents(entityJson.getAsJsonObject("components"), "components");
			entity.component_sets = parseComponentSets(entityJson);
			if (entity.ai != null) {
				entity.components.putIfAbsent("minecraft:ai", entity.ai);
			}

			Map<String, Component> initialActiveComponents = EntityStateResolver.resolveActiveComponents(
					entity,
					EntityStateResolver.createDefaultProperties(entity)
			);

			CollisionBoxComponent collisionBoxComponent = null;
			Component c = initialActiveComponents.get("minecraft:collision_box");
			if (c instanceof CollisionBoxComponent collisionBoxComponent1) {
				collisionBoxComponent = collisionBoxComponent1;
			}
			HealthComponent healthComponent = null;
			c = initialActiveComponents.get("minecraft:health");
			if (c instanceof HealthComponent healthComponent1) {
				healthComponent = healthComponent1;
			}

			MovementComponent movementComponent = null;
			c = initialActiveComponents.get("minecraft:movement");
			if (c instanceof MovementComponent movementComponent1) {
				movementComponent = movementComponent1;
			}

			BreathableComponent breathableComponent;
			c = initialActiveComponents.get("minecraft:breathable");
			if (c instanceof BreathableComponent breathableComponent1) {
				breathableComponent = breathableComponent1;
			} else {
				breathableComponent = null;
			}

			assert collisionBoxComponent != null;
			assert movementComponent != null;
			HealthComponent finalHealthComponent = healthComponent;
			assert finalHealthComponent != null;

			EntityType<EntityImpl> entityType = EntityRegistryBuilder.<EntityImpl>createBuilder(entityId)
					.entity((type, world) -> new EntityImpl(type, world, entity, finalHealthComponent.value, breathableComponent))
					.category(entity.entity_components.getCategory())
					.dimensions(EntityDimensions.fixed(collisionBoxComponent.width, collisionBoxComponent.height))
					.summonable(entity.description.summonable)
					.hasEgg(entity.description.spawnable)
					.egg(Integer.parseInt(baseColor, 16), Integer.parseInt(overlayColor, 16))
					.build();
			FabricDefaultAttributeRegistry.register(entityType, EntityUtils.createGenericEntityAttributes(finalHealthComponent.max, movementComponent.value, entity.attributes));
			register(ContentRegistries.ENTITIES, "entity", entityId, entity);
		} catch (Exception e) {
			failedRegistering("entity", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "entity";
	}

	private Map<String, Component> parseComponents(JsonObject source, String context) {
		Map<String, Component> parsed = new HashMap<>();
		if (source == null) return parsed;

		for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
			Identifier identifier = Identifier.tryParse(entry.getKey());
			if (identifier == null) {
				throw new JsonParseException("Invalid component identifier '" + entry.getKey() + "' in " + context);
			}
			Class<? extends Component> componentClass = Registries.ENTITY_COMPONENTS.getOptional(identifier).orElseThrow(() ->
					new JsonParseException("Unknown component \"" + entry.getKey() + "\" defined in " + context));
			parsed.put(identifier.toString(), BaseGson.GSON.fromJson(entry.getValue(), componentClass));
		}

		return parsed;
	}

	private Map<String, ComponentGroup> parseComponentSets(JsonObject root) {
		Map<String, ComponentGroup> sets = new HashMap<>();

		JsonObject componentSets = root.has("component_sets") && root.get("component_sets").isJsonObject()
				? root.getAsJsonObject("component_sets")
				: null;
		JsonObject componentGroups = root.has("component_groups") && root.get("component_groups").isJsonObject()
				? root.getAsJsonObject("component_groups")
				: null;

		JsonObject source = componentSets != null ? componentSets : componentGroups;
		if (source == null) return sets;

		for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
			if (!entry.getValue().isJsonObject()) continue;
			JsonObject rawSet = entry.getValue().getAsJsonObject();
			JsonObject componentObject = rawSet.has("components") && rawSet.get("components").isJsonObject()
					? rawSet.getAsJsonObject("components")
					: rawSet;

			ComponentGroup group = new ComponentGroup();
			group.components.putAll(parseComponents(componentObject, "component set '" + entry.getKey() + "'"));
			sets.put(entry.getKey(), group);
		}

		return sets;
	}
}
