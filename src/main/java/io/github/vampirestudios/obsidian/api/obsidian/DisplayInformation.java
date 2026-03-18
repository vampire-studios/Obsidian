package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class DisplayInformation {

    @SerializedName("block_model")
    @blue.endless.jankson.annotation.SerializedName("block_model")
    public JsonElement blockModel;

    @SerializedName("powered_model")
    @blue.endless.jankson.annotation.SerializedName("powered_model")
    public JsonElement poweredModel;

    @Deprecated
    public JsonElement model;

    public TextureAndModelInformation hangingModel;
    public TextureAndModelInformation trapdoorBottomModel;
    public TextureAndModelInformation trapdoorOpenModel;
    public TextureAndModelInformation trapdoorTopModel;
    public TextureAndModelInformation doorBottomModel;
    public TextureAndModelInformation doorBottomHingeModel;
    public TextureAndModelInformation doorTopModel;
    public TextureAndModelInformation doorTopHingeModel;
    public TextureAndModelInformation onModel;
    public TextureAndModelInformation offModel;
    public TextureAndModelInformation stickyPiston;

    @SerializedName("item_model")
    @blue.endless.jankson.annotation.SerializedName("item_model")
    public JsonElement itemModel;

    @SerializedName("block_state")
    @blue.endless.jankson.annotation.SerializedName("block_state")
    public BlockProperty blockState;

    /**
     * Extra clockwise Y-rotation (in degrees, multiples of 90) applied to the model
     * for all facing variants of HORIZONTAL_DIRECTIONAL and DIRECTIONAL blocks.
     * Use 90 to rotate the model one step clockwise relative to the default orientation.
     */
    @SerializedName("model_rotation_offset")
    @blue.endless.jankson.annotation.SerializedName("model_rotation_offset")
    public int model_rotation_offset = 0;

    public TextureAndModelInformation getBlockModel() {
        return parseModel(blockModel);
    }

    public TextureAndModelInformation getPoweredModel() {
        return parseModel(poweredModel);
    }

    public TextureAndModelInformation getItemModel() {
        return parseModel(itemModel);
    }

    public TextureAndModelInformation getModel() {
        return parseModel(model);
    }

    public boolean hasItemModelObject() { return itemModel != null && itemModel.isJsonObject(); }
    public boolean hasItemModelString() { return itemModel != null && itemModel.isJsonPrimitive() && itemModel.getAsJsonPrimitive().isString(); }

    public boolean hasBlockModelObject() { return blockModel != null && blockModel.isJsonObject(); }
    public boolean hasBlockModelString() { return blockModel != null && blockModel.isJsonPrimitive() && blockModel.getAsJsonPrimitive().isString(); }

    public boolean hasPoweredModelObject() { return poweredModel != null && poweredModel.isJsonObject(); }
    public boolean hasPoweredModelString() { return poweredModel != null && poweredModel.isJsonPrimitive() && poweredModel.getAsJsonPrimitive().isString(); }

    public boolean hasLegacyModelObject() { return model != null && model.isJsonObject(); }
    public boolean hasLegacyModelString() { return model != null && model.isJsonPrimitive() && model.getAsJsonPrimitive().isString(); }

    /**
     * Accepts:
     * - "namespace:path"
     * - { "parent": "namespace:path", "textures": { "0": "...", "particle": "..." } }
     * Also tolerates aliases:
     * - { "model": "..." } or { "id": "..." } treated as parent
     */
    private static TextureAndModelInformation parseModel(JsonElement e) {
        if (e == null || e.isJsonNull()) return null;

        if (e.isJsonPrimitive()) {
            JsonPrimitive p = e.getAsJsonPrimitive();
            if (!p.isString()) {
                throw new IllegalArgumentException("Model must be a string Identifier or an object");
            }
            TextureAndModelInformation info = new TextureAndModelInformation();
            info.parent = Identifier.parse(p.getAsString());
            return info;
        }

        if (!e.isJsonObject()) {
            throw new IllegalArgumentException("Model must be a string Identifier or an object");
        }

        JsonObject o = e.getAsJsonObject();
        TextureAndModelInformation info = new TextureAndModelInformation();

        Identifier parent = readIdentifier(o, "parent");
        if (parent == null) parent = readIdentifier(o, "model");
        if (parent == null) parent = readIdentifier(o, "id");
        info.parent = parent;

		info.textures = readTextures(o, "textures");

        return info;
    }

    private static Identifier readIdentifier(JsonObject o, String key) {
        if (!o.has(key) || o.get(key).isJsonNull()) return null;
        JsonElement e = o.get(key);
        if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("Field '" + key + "' must be a string Identifier");
        }
        return Identifier.parse(e.getAsString());
    }

    private static Map<String, Identifier> readTextures(JsonObject o, String key) {
        if (!o.has(key) || o.get(key).isJsonNull()) return null;

        JsonElement e = o.get(key);
        if (!e.isJsonObject()) {
            throw new IllegalArgumentException("Field '" + key + "' must be an object of texture variables");
        }

        JsonObject texObj = e.getAsJsonObject();
        Map<String, Identifier> out = new LinkedHashMap<>();

        for (Map.Entry<String, JsonElement> entry : texObj.entrySet()) {
            String var = entry.getKey();
            JsonElement val = entry.getValue();

            if (val == null || val.isJsonNull()) continue;

            if (!val.isJsonPrimitive() || !val.getAsJsonPrimitive().isString()) {
                throw new IllegalArgumentException("Texture '" + var + "' must be a string Identifier");
            }

            out.put(var, Identifier.parse(val.getAsString()));
        }

        return out;
    }

    public Identifier resolveItemDefinitionModelId(Identifier blockId) {
        // output ids (where YOUR generator writes model json)
        Identifier outBlockModel = Utils.prependToPath(blockId, "block/");
        Identifier outItemModel  = Utils.prependToPath(blockId, "item/");

        // 1) item_model present:
        // - string => direct reference (already-existing model id)
        // - object => you will generate models/item/<id>.json, so reference that output id
        if (itemModel != null && !itemModel.isJsonNull()) {
            if (hasItemModelString()) return Identifier.parse(itemModel.getAsString());
            return outItemModel; // object
        }

        // 2) block_model present:
        // - string => direct reference
        // - object => you generate block model at outBlockModel, so reference that output id
        if (blockModel != null && !blockModel.isJsonNull()) {
            if (hasBlockModelString()) return Identifier.parse(blockModel.getAsString());
            return outBlockModel;
        }

        // 3) legacy model present:
        if (model != null && !model.isJsonNull()) {
            if (hasLegacyModelString()) return Identifier.parse(model.getAsString());
            return outBlockModel;
        }

        return outBlockModel;
    }

    public static class Property {
        public Identifier model;
        public int x;
        public int y;
        public int z;
    }

}
