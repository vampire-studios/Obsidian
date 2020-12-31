package io.github.vampirestudios.obsidian.api.obsidian.entity;

import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
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
    public EntityModel<EntityImpl> getEntityModel(EntityRendererFactory.Context context) {
        switch (vanilla_entity_type.toString()) {
            case "minecraft:pig":
                return new PigEntityModel<>(context.getPart(EntityModelLayers.PIG));
            case "minecraft:villager":
                return new VillagerResemblingModel<>(context.getPart(EntityModelLayers.VILLAGER));
            case "minecraft:chicken":
                return new ChickenEntityModel<>(context.getPart(EntityModelLayers.CHICKEN));
            case "minecraft:bear":
                return new PolarBearEntityModel(context.getPart(EntityModelLayers.POLAR_BEAR));
            case "minecraft:squid":
                return new SquidEntityModel<>(context.getPart(EntityModelLayers.SQUID));
            case "minecraft:zombie":
                return new ZombieEntityModel(context.getPart(EntityModelLayers.ZOMBIE));
            case "minecraft:skeleton":
                return new SkeletonEntityModel(context.getPart(EntityModelLayers.SKELETON));
            case "minecraft:fox":
                return new FoxEntityModel(context.getPart(EntityModelLayers.FOX));
            case "minecraft:horse":
                return new HorseEntityModel(context.getPart(EntityModelLayers.HORSE));
            case "minecraft:cow":
            default:
                return new CowEntityModel<>(context.getPart(EntityModelLayers.COW));

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
