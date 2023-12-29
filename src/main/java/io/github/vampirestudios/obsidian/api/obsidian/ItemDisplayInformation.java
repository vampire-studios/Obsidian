package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ItemDisplayInformation {

    @Deprecated public TextureAndModelInformation model;
    public TextureAndModelInformation itemModel;
    public TextureAndModelInformation blockingModel;
    public TextureAndModelInformation[] pullingModels;
    public TextureAndModelInformation chargedModel;
    public TextureAndModelInformation fireworkModel;
    public TextureAndModelInformation castModel;
    public TextureAndModelInformation[] damagedModels;

    public Object testModel;
    public boolean generateModel = true;
    public Map<String, ResourceLocation> textures;
    public ResourceLocation parentModel;

    public TextureAndModelInformation getModel() {
        return switch(testModel) {
            case TextureAndModelInformation information -> information;
            case String location -> {
                TextureAndModelInformation textureAndModelInformation = new TextureAndModelInformation();
                textureAndModelInformation.parent = ResourceLocation.tryParse(location);
                yield textureAndModelInformation;
            }
            case null, default -> null;
        };
    }

}
