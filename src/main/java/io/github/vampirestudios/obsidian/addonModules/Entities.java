package io.github.vampirestudios.obsidian.addonModules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.BreathableComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.CollisionBoxComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.HealthComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.MovementComponent;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import io.github.vampirestudios.obsidian.utils.EntityRegistryBuilder;
import io.github.vampirestudios.obsidian.utils.EntityUtils;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Entities implements AddonModule {
    @Override
    public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
        JsonObject entityJson = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);
        Entity entity = Obsidian.GSON.fromJson(entityJson, Entity.class);
        try {
            if(entity == null) return;
            String baseColor = entity.information.spawn_egg.base_color.replace("#", "").replace("0x", "");
            String overlayColor = entity.information.spawn_egg.overlay_color.replace("#", "").replace("0x", "");
            entity.components = new HashMap<>();
            JsonObject components = JsonHelper.getObject(entityJson, "components");
            for (Map.Entry<String, JsonElement> entry : components.entrySet()) {
                Identifier identifier = new Identifier(entry.getKey());
                Class<? extends Component> componentClass = Obsidian.ENTITY_COMPONENT_REGISTRY.getOrEmpty(identifier).orElseThrow(() -> new JsonParseException("Unknown component \"" + entry.getKey() + "\" defined in entity json"));

                entity.components.put(identifier.toString(), Obsidian.GSON.fromJson(entry.getValue(), componentClass));
            }

            CollisionBoxComponent collisionBoxComponent = null;
            Component c = entity.components.get("minecraft:collision_box");
            if (c instanceof CollisionBoxComponent) {
                collisionBoxComponent = (CollisionBoxComponent) c;
            }
            HealthComponent healthComponent = null;
            c = entity.components.get("minecraft:health");
            if (c instanceof HealthComponent) {
                healthComponent = (HealthComponent) c;
            }

            MovementComponent movementComponent = null;
            c = entity.components.get("minecraft:movement");
            if (c instanceof MovementComponent) {
                movementComponent = (MovementComponent) c;
            }

            BreathableComponent breathableComponent = null;
            c = entity.components.get("minecraft:breathable");
            if (c instanceof BreathableComponent) {
                breathableComponent = (BreathableComponent) c;
            }

            assert collisionBoxComponent != null;
            assert movementComponent != null;
            HealthComponent finalHealthComponent = healthComponent;
            BreathableComponent finalBreathableComponent = breathableComponent;
            EntityType<EntityImpl> entityType = EntityRegistryBuilder.<EntityImpl>createBuilder(entity.information.identifier)
                    .entity((type, world) -> new EntityImpl(type, world, entity, finalHealthComponent.value, finalBreathableComponent))
                    .category(entity.entity_components.getCategory())
                    .dimensions(EntityDimensions.fixed(collisionBoxComponent.width, collisionBoxComponent.height))
                    .summonable(entity.information.summonable)
                    .hasEgg(entity.information.spawnable)
                    .egg(Integer.parseInt(baseColor, 16), Integer.parseInt(overlayColor, 16))
                    .build();
            FabricDefaultAttributeRegistry.register(entityType, EntityUtils.createGenericEntityAttributes(finalHealthComponent.max, movementComponent.value));
            register(ENTITIES, "entity", entity.information.identifier.toString(), entity);
        } catch (Exception e) {
            failedRegistering("entity", entity.information.identifier.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "entities";
    }
}
