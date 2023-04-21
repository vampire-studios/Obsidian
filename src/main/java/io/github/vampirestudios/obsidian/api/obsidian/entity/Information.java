package io.github.vampirestudios.obsidian.api.obsidian.entity;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityModelImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import java.util.Optional;

public class Information {

    public ResourceLocation identifier;
    public String name;
    public boolean spawnable;
    public boolean summonable;
    public ResourceLocation vanilla_entity_type = new ResourceLocation("minecraft:pig");
    public boolean custom_model;
    public ResourceLocation textureLocation;
    public ResourceLocation entityModelPath;

    public SpawnEgg spawn_egg;

    public EntityModel<EntityImpl> getNewEntityModel(EntityRendererProvider.Context context) {
        EntityModel<EntityImpl> entityModel;
        Optional<io.github.vampirestudios.obsidian.api.obsidian.EntityModel> model = ContentRegistries.ENTITY_MODELS.getOptional(entityModelPath);
        if(model.isPresent()) {
            EntityModelLayerRegistry.registerModelLayer(ModelLayers.register("obsidian:" + entityModelPath.toString()),
                    () -> model.get().getTexturedModelData());
            entityModel = new EntityModelImpl(model.get());
        } else {
            entityModel = getEntityModel(context);
        }
        return entityModel;
    }

    @Environment(EnvType.CLIENT)
    public EntityModel<EntityImpl> getEntityModel(EntityRendererProvider.Context context) {
        return switch (vanilla_entity_type.toString()) {
            case "minecraft:pig" -> new PigModel<>(context.bakeLayer(ModelLayers.PIG));
            case "minecraft:villager" -> new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER));
            case "minecraft:chicken" -> new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN));
            case "minecraft:squid" -> new SquidModel<>(context.bakeLayer(ModelLayers.SQUID));
            case "minecraft:biped" -> new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE));
            case "minecraft:skeleton" -> new SkeletonModel(context.bakeLayer(ModelLayers.SKELETON));
            default -> new CowModel<>(context.bakeLayer(ModelLayers.COW));
        };
    }

    public ResourceLocation getEntityTexture() {
        if (custom_model) {
            return textureLocation;
        } else {
            return switch (vanilla_entity_type.toString()) {
                case "minecraft:pig" -> new ResourceLocation("textures/entity/pig/pig.png");
                case "minecraft:villager" -> new ResourceLocation("textures/entity/villager/villager.png");
                case "minecraft:chicken" -> new ResourceLocation("textures/entity/chicken.png");
                case "minecraft:bear" -> new ResourceLocation("textures/entity/bear/polarbear.png");
                case "minecraft:squid" -> new ResourceLocation("textures/entity/squid.png");
                case "minecraft:zombie" -> new ResourceLocation("textures/entity/zombie/zombie.png");
                case "minecraft:skeleton" -> new ResourceLocation("textures/entity/skeleton/skeleton.png");
                case "minecraft:fox" -> new ResourceLocation("textures/entity/fox/fox.png");
                case "minecraft:horse" -> new ResourceLocation("textures/entity/horse/horse_black.png");
                default -> new ResourceLocation("textures/entity/cow/cow.png");
            };
        }
    }

}
