package io.github.vampirestudios.obsidian.api.entity;

import io.github.vampirestudios.obsidian.minecraft.EntityImpl;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.util.Identifier;

public class Entity {

    public Identifier identifier;
    public boolean spawnable;
    public boolean summonable;
    public boolean experimental;
    public Identifier vanilla_entity_type;

    public SpawnEgg spawn_egg;

    public EntityComponents components;

    public EntityModel<EntityImpl> getEntityModel() {
        switch (vanilla_entity_type.toString()) {
            case "minecraft:pig":
                return new PigEntityModel<>();
            case "minecraft:villager":
                return new VillagerResemblingModel<>(1.0F);
            case "minecraft:chicken":
                return new ChickenEntityModel<>();
            case "minecraft:cow":
            default:
                return new CowEntityModel<>();
        }
    }

    public Identifier getEntityTexture() {
        switch (vanilla_entity_type.toString()) {
            case "minecraft:pig":
                return new Identifier("textures/entity/pig/pig.png");
            case "minecraft:villager":
                return new Identifier("textures/entity/villager/villager.png");
            case "minecraft:chicken":
                return new Identifier("textures/entity/chicken.png");
            case "minecraft:cow":
            default:
                return new Identifier("textures/entity/cow/cow.png");
        }
    }

}