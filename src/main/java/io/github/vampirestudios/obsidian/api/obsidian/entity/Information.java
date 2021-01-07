package io.github.vampirestudios.obsidian.api.obsidian.entity;

import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.util.Identifier;

public class Information {

    public Identifier identifier;
    public boolean spawnable;
    public boolean summonable;
    public Identifier vanilla_entity_type;
    public boolean custom_model;
    public CustomEntityModel entity_model;

    public SpawnEgg spawn_egg;


    @Environment(EnvType.CLIENT)
    public EntityModel<EntityImpl> getEntityModel(EntityRenderDispatcher context) {
        switch (vanilla_entity_type.toString()) {
            case "minecraft:pig":
                return new PigEntityModel<>();
            case "minecraft:villager":
                return new VillagerResemblingModel<>(1.0F);
            case "minecraft:chicken":
                return new ChickenEntityModel<>();
            case "minecraft:bear":
                return new PolarBearEntityModel();
            case "minecraft:squid":
                return new SquidEntityModel<>();
            case "minecraft:zombie":
                return new ZombieEntityModel(1.0F, false);
            case "minecraft:skeleton":
                return new SkeletonEntityModel();
            case "minecraft:fox":
                return new FoxEntityModel();
            case "minecraft:horse":
                return new HorseEntityModel(1.0F);
            case "minecraft:cow":
            default:
                return new CowEntityModel<>();

        }
    }

    public Identifier getEntityTexture() {
        if (custom_model) {
            return entity_model.textureLocation;
        } else {
            switch (vanilla_entity_type.toString()) {
                case "minecraft:pig":
                    return new Identifier("textures/entity/pig/pig.png");
                case "minecraft:villager":
                    return new Identifier("textures/entity/villager/villager.png");
                case "minecraft:chicken":
                    return new Identifier("textures/entity/chicken.png");
                case "minecraft:bear":
                    return new Identifier("textures/entity/bear/polarbear.png");
                case "minecraft:squid":
                    return new Identifier("textures/entity/squid.png");
                case "minecraft:zombie":
                    return new Identifier("textures/entity/zombie/zombie.png");
                case "minecraft:skeleton":
                    return new Identifier("textures/entity/skeleton/skeleton.png");
                case "minecraft:fox":
                    return new Identifier("textures/entity/fox/fox.png");
                case "minecraft:horse":
                    return new Identifier("textures/entity/horse/horse_black.png");
                case "minecraft:cow":
                default:
                    return new Identifier("textures/entity/cow/cow.png");
            }
        }
    }

}
