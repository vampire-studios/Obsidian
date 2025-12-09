package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.resources.Identifier;

import java.util.Map;

public class TextureAndModelInformation {

    public Map<String, Identifier> textures;
    public Identifier parent;

    public TextureAndModelInformation(Identifier parent) {
        this.parent = parent;
    }

    public TextureAndModelInformation() {
    }

    public Map<String, Identifier> getTextures() {
        return textures;
    }

    public void setTextures(Map<String, Identifier> textures) {
        this.textures = textures;
    }

    public Identifier getParent() {
        return parent;
    }

    public void setParent(Identifier parent) {
        this.parent = parent;
    }
}