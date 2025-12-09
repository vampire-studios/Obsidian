package io.github.vampirestudios.obsidian.api.obsidian.entity;

import net.minecraft.resources.Identifier;

public class Information {

    public Identifier identifier;
    public String name;
    public boolean spawnable;
    public boolean summonable;
    public Identifier vanilla_entity_type = Identifier.withDefaultNamespace("pig");
    public boolean custom_model;
    public Identifier textureLocation;
    public Identifier entityModelPath;

    public SpawnEgg spawn_egg;

    /*public EntityModel<EntityImpl> getNewEntityModel(EntityRendererProvider.Context context) {
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
    }*/

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
