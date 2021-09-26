package io.github.vampirestudios.obsidian.api.obsidian.entity;

import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityModelImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class Information {

    public Identifier identifier;
    public String name;
    public boolean spawnable;
    public boolean summonable;
    public Identifier vanilla_entity_type = new Identifier("minecraft:pig");
    public boolean custom_model;
    public Identifier textureLocation;
    public Identifier entityModelPath;

    public SpawnEgg spawn_egg;

    public EntityModel<EntityImpl> getNewEntityModel(EntityRendererFactory.Context context) {
        EntityModel<EntityImpl> entityModel;
        Optional<io.github.vampirestudios.obsidian.api.obsidian.EntityModel> model = ObsidianAddonLoader.ENTITY_MODELS.getOrEmpty(entityModelPath);
        if(model.isPresent()) {
            entityModel = new EntityModelImpl<>(model.get());
        } else {
            entityModel = getEntityModel(context);
        }
        return entityModel;
    }

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
            return textureLocation;
        } else {
            return switch (vanilla_entity_type.toString()) {
                case "minecraft:pig" -> new Identifier("textures/entity/pig/pig.png");
                case "minecraft:villager" -> new Identifier("textures/entity/villager/villager.png");
                case "minecraft:chicken" -> new Identifier("textures/entity/chicken.png");
                case "minecraft:bear" -> new Identifier("textures/entity/bear/polarbear.png");
                case "minecraft:squid" -> new Identifier("textures/entity/squid.png");
                case "minecraft:zombie" -> new Identifier("textures/entity/zombie/zombie.png");
                case "minecraft:skeleton" -> new Identifier("textures/entity/skeleton/skeleton.png");
                case "minecraft:fox" -> new Identifier("textures/entity/fox/fox.png");
                case "minecraft:horse" -> new Identifier("textures/entity/horse/horse_black.png");
                default -> new Identifier("textures/entity/cow/cow.png");
            };
        }
    }

}
