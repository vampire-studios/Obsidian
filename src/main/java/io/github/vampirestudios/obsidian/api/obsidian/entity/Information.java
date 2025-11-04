package io.github.vampirestudios.obsidian.api.obsidian.entity;

import net.minecraft.resources.ResourceLocation;

public class Information {

    public ResourceLocation identifier;
    public String name;
    public boolean spawnable;
    public boolean summonable;
    public ResourceLocation vanilla_entity_type = ResourceLocation.withDefaultNamespace("pig");
    public boolean custom_model;
    public ResourceLocation textureLocation;
    public ResourceLocation entityModelPath;

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

    public ResourceLocation getEntityTexture() {
        if (custom_model) {
            return textureLocation;
        } else {
            return switch (vanilla_entity_type.toString()) {
                case "minecraft:pig" -> ResourceLocation.withDefaultNamespace("textures/entity/pig/pig.png");
                case "minecraft:villager" -> ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png");
                case "minecraft:chicken" -> ResourceLocation.withDefaultNamespace("textures/entity/chicken.png");
                case "minecraft:bear" -> ResourceLocation.withDefaultNamespace("textures/entity/bear/polarbear.png");
                case "minecraft:squid" -> ResourceLocation.withDefaultNamespace("textures/entity/squid.png");
                case "minecraft:zombie" -> ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
                case "minecraft:skeleton" -> ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png");
                case "minecraft:fox" -> ResourceLocation.withDefaultNamespace("textures/entity/fox/fox.png");
                case "minecraft:horse" -> ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_black.png");
                default -> ResourceLocation.withDefaultNamespace("textures/entity/cow/cow.png");
            };
        }
    }

}
