package io.github.vampirestudios.obsidian.api.obsidian;

import blue.endless.jankson.annotation.SerializedName;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemDisplayInformation {

    @Deprecated public TextureAndModelInformation model;
    @SerializedName("blocking_model")
    @com.google.gson.annotations.SerializedName("blocking_model")
    public Object blockingModel;
    public TextureAndModelInformation[] pullingModels;
    public TextureAndModelInformation chargedModel;
    public TextureAndModelInformation fireworkModel;
    public TextureAndModelInformation castModel;
    public TextureAndModelInformation[] damagedModels;

    public Object testModel;
    public boolean generateModel = true;
    public Map<String, Identifier> textures;
    public Identifier parentModel;

    @SerializedName("item_model")
    @com.google.gson.annotations.SerializedName("item_model")
    public Object itemModel;

    public Optional<TextureAndModelInformation> getItemModel() {
        if (itemModel instanceof TextureAndModelInformation info) {
            return Optional.of(info);
        } else if(itemModel instanceof Identifier Identifier) {
            return Optional.of(new TextureAndModelInformation(Identifier));
        } else if (itemModel instanceof Map<?, ?> modelMap) {
			TextureAndModelInformation textureAndModelInformation = new TextureAndModelInformation();

            if (modelMap.containsKey("parent")) {
                Object parent = modelMap.get("parent");
                if (parent instanceof String) {
                    textureAndModelInformation.setParent(Identifier.tryParse((String) parent));
                }
            }

            if (modelMap.containsKey("textures")) {
                Object textures = modelMap.get("textures");
                if (textures instanceof Map<?, ?> texturesMap) {
                    textureAndModelInformation.setTextures(parseTextures(texturesMap));
                }
            }
            return Optional.of(textureAndModelInformation);
        } else if (itemModel instanceof String s) {
            try {
                return Optional.of(new TextureAndModelInformation(Identifier.tryParse(s)));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    // Helper method to parse textures
    private Map<String, Identifier> parseTextures(Map<?, ?> texturesMap) {
        Map<String, Identifier> textures = new HashMap<>();
        for (Map.Entry<?, ?> entry : texturesMap.entrySet()) {
            if (entry.getKey() instanceof String key && entry.getValue() instanceof String value) {
                Identifier parsedLocation = Identifier.tryParse(value);
                if (parsedLocation != null) {
                    textures.put(key, parsedLocation);
                }
            }
            if (entry.getKey() instanceof String key && entry.getValue() instanceof Identifier id) {
                if (id != null) {
                    textures.put(key, id);
                }
            }
        }
        return textures;
    }

    public TextureAndModelInformation getBlockingModel() {
        if (blockingModel instanceof TextureAndModelInformation info) {
            return info;
        } else if (blockingModel instanceof String s) {
            TextureAndModelInformation textureAndModelInformation = new TextureAndModelInformation();
            textureAndModelInformation.parent = Identifier.tryParse(s);
            return textureAndModelInformation;
        } else {
            return null;
        }
    }

    public TextureAndModelInformation getModel() {
        if (testModel instanceof TextureAndModelInformation info) {
            return info;
        } else if (testModel instanceof String s) {
            TextureAndModelInformation textureAndModelInformation = new TextureAndModelInformation();
            textureAndModelInformation.parent = Identifier.tryParse(s);
            return textureAndModelInformation;
        } else {
            return null;
        }
    }

}
