package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class TextureAndModelInformation {

    public Map<String, ResourceLocation> textures;
    public ResourceLocation parent;

    public TextureAndModelInformation(ResourceLocation parent) {
        this.parent = parent;
    }

    public TextureAndModelInformation() {
    }

    public Map<String, ResourceLocation> getTextures() {
        return textures;
    }

    public void setTextures(Map<String, ResourceLocation> textures) {
        this.textures = textures;
    }

    public ResourceLocation getParent() {
        return parent;
    }

    public void setParent(ResourceLocation parent) {
        this.parent = parent;
    }
}