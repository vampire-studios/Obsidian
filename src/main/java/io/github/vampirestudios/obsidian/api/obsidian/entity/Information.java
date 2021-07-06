package io.github.vampirestudios.obsidian.api.obsidian.entity;

import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityModelImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.util.Identifier;

public class Information {

    public Identifier identifier;
    public String name;
    public boolean spawnable;
    public boolean summonable;
    public Identifier vanilla_entity_type;
    public boolean custom_model;
    public Identifier textureLocation;
    public Identifier entityModelPath;

    public SpawnEgg spawn_egg;

    public EntityModel<EntityImpl> getNewEntityModel(EntityRendererFactory.Context context) {
        var ref = new Object() {
            EntityModel<EntityImpl> entityModel = getEntityModel(context);
        };
        EntityModelLayer entityModelLayer = EntityModelLayers.registerMain(identifier.getPath());
        EntityModelLayerRegistry.registerModelLayer(entityModelLayer, EntityModelImpl::getTexturedModelData);
        ObsidianAddonLoader.ENTITY_MODELS.forEach(entityModel1 -> {
            if(entityModel1.name == entityModelPath && entityModelPath != null) {
                if (ref.entityModel != null) {
                    ref.entityModel = new EntityModelImpl<>(context.getPart(entityModelLayer), entityModel1);
                }
            }
        });
        return ref.entityModel;
    }

    @Environment(EnvType.CLIENT)
    public EntityModel<EntityImpl> getEntityModel(EntityRendererFactory.Context context) {
        return switch (vanilla_entity_type.toString()) {
            case "minecraft:pig" -> new PigEntityModel<>(context.getPart(EntityModelLayers.PIG));
            case "minecraft:villager" -> new VillagerResemblingModel<>(context.getPart(EntityModelLayers.VILLAGER));
            case "minecraft:chicken" -> new ChickenEntityModel<>(context.getPart(EntityModelLayers.CHICKEN));
            case "minecraft:bear" -> new PolarBearEntityModel(context.getPart(EntityModelLayers.POLAR_BEAR));
            case "minecraft:squid" -> new SquidEntityModel<>(context.getPart(EntityModelLayers.SQUID));
            case "minecraft:zombie" -> new ZombieEntityModel(context.getPart(EntityModelLayers.ZOMBIE));
            case "minecraft:skeleton" -> new SkeletonEntityModel(context.getPart(EntityModelLayers.SKELETON));
            case "minecraft:fox" -> new FoxEntityModel(context.getPart(EntityModelLayers.FOX));
            case "minecraft:horse" -> new HorseEntityModel(context.getPart(EntityModelLayers.HORSE));
            default -> new CowEntityModel<>(context.getPart(EntityModelLayers.COW));
        };
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
