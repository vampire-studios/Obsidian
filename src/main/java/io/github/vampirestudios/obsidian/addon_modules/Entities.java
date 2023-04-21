package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.BreathableComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.CollisionBoxComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.HealthComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.MovementComponent;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.EntityRegistryBuilder;
import io.github.vampirestudios.obsidian.utils.EntityUtils;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Entities implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        JsonObject entityJson = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);
        Entity entity = Obsidian.GSON.fromJson(entityJson, Entity.class);
        try {
            if (entity == null) return;
            String baseColor = entity.information.spawn_egg.base_color.replace("#", "").replace("0x", "");
            String overlayColor = entity.information.spawn_egg.overlay_color.replace("#", "").replace("0x", "");
            entity.components = new HashMap<>();
            JsonObject components = GsonHelper.getAsJsonObject(entityJson, "components");
            for (Map.Entry<String, JsonElement> entry : components.entrySet()) {
                ResourceLocation identifier = new ResourceLocation(entry.getKey());
                Class<? extends Component> componentClass = Registries.ENTITY_COMPONENT_REGISTRY.getOptional(identifier).orElseThrow(() ->
                        new JsonParseException("Unknown component \"" + entry.getKey() + "\" defined in entity json"));

                entity.components.put(identifier.toString(), Obsidian.GSON.fromJson(entry.getValue(), componentClass));
            }

            CollisionBoxComponent collisionBoxComponent = null;
            Component c = entity.components.get("minecraft:collision_box");
            if (c instanceof CollisionBoxComponent collisionBoxComponent1) {
                collisionBoxComponent = collisionBoxComponent1;
            }
            HealthComponent healthComponent = null;
            c = entity.components.get("minecraft:health");
            if (c instanceof HealthComponent healthComponent1) {
                healthComponent = healthComponent1;
            }

            MovementComponent movementComponent = null;
            c = entity.components.get("minecraft:movement");
            if (c instanceof MovementComponent movementComponent1) {
                movementComponent = movementComponent1;
            }

            BreathableComponent breathableComponent = null;
            c = entity.components.get("minecraft:breathable");
            if (c instanceof BreathableComponent breathableComponent1) {
                breathableComponent = breathableComponent1;
            }

            assert collisionBoxComponent != null;
            assert movementComponent != null;
            HealthComponent finalHealthComponent = healthComponent;
            BreathableComponent finalBreathableComponent = breathableComponent;
            assert finalHealthComponent != null;

            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    entity.information.identifier,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (entity.information.identifier == null) entity.information.identifier = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

            EntityType<EntityImpl> entityType = EntityRegistryBuilder.<EntityImpl>createBuilder(identifier)
                    .entity((type, world) -> new EntityImpl(type, world, entity, finalHealthComponent.value, finalBreathableComponent))
                    .category(entity.entity_components.getCategory())
                    .dimensions(EntityDimensions.fixed(collisionBoxComponent.width, collisionBoxComponent.height))
                    .summonable(entity.information.summonable)
                    .hasEgg(entity.information.spawnable)
                    .egg(Integer.parseInt(baseColor, 16), Integer.parseInt(overlayColor, 16))
                    .build();
            FabricDefaultAttributeRegistry.register(entityType, EntityUtils.createGenericEntityAttributes(finalHealthComponent.max, movementComponent.value));
            register(ContentRegistries.ENTITIES, "entity", identifier, entity);
        } catch (Exception e) {
            failedRegistering("entity", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "entities";
    }
}
