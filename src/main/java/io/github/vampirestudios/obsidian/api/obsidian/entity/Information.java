package io.github.vampirestudios.obsidian.api.obsidian.entity;

import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityModelImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public class Information {

    public Identifier identifier;
    public transient Identifier id;
    public String name;
    public boolean spawnable;
    public boolean summonable;
    public Identifier vanilla_entity_type = Identifier.withDefaultNamespace("pig");
    public boolean custom_model;
    public Identifier textureLocation;
    public Identifier entityModelPath;

    public SpawnEgg spawn_egg;

    public EntityModelImpl getNewEntityModel(EntityRendererProvider.Context context) {
        if (entityModelPath != null) {
            Optional<io.github.vampirestudios.obsidian.api.obsidian.EntityModel> model = ContentRegistries.ENTITY_MODELS.getOptional(entityModelPath);
            if (model.isPresent()) {
                return new EntityModelImpl(model.get());
            }
        }
        return new EntityModelImpl(context.bakeLayer(getVanillaModelLayer()));
    }

    private net.minecraft.client.model.geom.ModelLayerLocation getVanillaModelLayer() {
        return switch (vanilla_entity_type.toString()) {
            case "minecraft:pig" -> ModelLayers.PIG;
            case "minecraft:villager" -> ModelLayers.VILLAGER;
            case "minecraft:chicken" -> ModelLayers.CHICKEN;
            case "minecraft:squid" -> ModelLayers.SQUID;
            case "minecraft:zombie" -> ModelLayers.ZOMBIE;
            case "minecraft:skeleton" -> ModelLayers.SKELETON;
            case "minecraft:fox" -> ModelLayers.FOX;
            case "minecraft:horse" -> ModelLayers.HORSE;
            case "minecraft:bear" -> ModelLayers.POLAR_BEAR;
            default -> ModelLayers.COW;
        };
    }

    public Identifier getEntityTexture() {
        if (custom_model) {
            return textureLocation;
        } else {
            return switch (vanilla_entity_type.toString()) {
                case "minecraft:pig" -> Identifier.withDefaultNamespace("textures/entity/pig/pig.png");
                case "minecraft:villager" -> Identifier.withDefaultNamespace("textures/entity/villager/villager.png");
                case "minecraft:chicken" -> Identifier.withDefaultNamespace("textures/entity/chicken.png");
                case "minecraft:bear" -> Identifier.withDefaultNamespace("textures/entity/bear/polarbear.png");
                case "minecraft:squid" -> Identifier.withDefaultNamespace("textures/entity/squid.png");
                case "minecraft:zombie" -> Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png");
                case "minecraft:skeleton" -> Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png");
                case "minecraft:fox" -> Identifier.withDefaultNamespace("textures/entity/fox/fox.png");
                case "minecraft:horse" -> Identifier.withDefaultNamespace("textures/entity/horse/horse_black.png");
                default -> Identifier.withDefaultNamespace("textures/entity/cow/cow.png");
            };
        }
    }

}
