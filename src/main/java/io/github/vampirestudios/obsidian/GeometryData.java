package io.github.vampirestudios.obsidian;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import io.github.vampirestudios.obsidian.client.ClientInit;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GeometryData {

    private static final String FORMAT_VERSION = "1.12.0";

    protected Identifier id;
    protected int textureWidth, textureHeight;

    private final HashMap<String, GeometryBone> bones = new HashMap<>();

    public GeometryData(int textureWidth, int texHeight, List<GeometryBone> bones) {
        this.textureWidth = textureWidth; this.textureHeight = texHeight;

        bones.forEach(b -> {
            this.bones.put(b.bone_id, b);
            if(b.hasParent)
                this.bones.get(b.parent).addChild(b);
        });
    }

    public List<GeometryBone> getRootBones() {
        return bones.values().stream().filter(b -> !b.hasParent).collect(Collectors.toList());
    }

    public GeometryBone getBone(String part) {
        return bones.get(part);
    }

    public List<GeometryBone> getAllBones() { return Lists.newArrayList(bones.values()); }

    //TODO use internal Resource Manager

    public static GeometryData load(ResourceManager resourceManager, Identifier identifier) {
        try {
            Resource resource = resourceManager.getResource(identifier);

            GeometryData data;
            try(JsonReader reader = ClientInit.GSON_CLIENT.newJsonReader(new InputStreamReader(resource.getInputStream()))) {
                data = ClientInit.GSON_CLIENT.fromJson(reader, GeometryData.class);
            } catch(JsonParseException e) {
                System.out.println("Failed to parse model json!");
                e.printStackTrace();
                return ClientInit.INSTANCE.geometryManager.getModelData(GeometryManager.MISSING_IDENTIFIER);
            }

            return data;
        } catch (IOException var20) {
            var20.printStackTrace();
            return ClientInit.INSTANCE.geometryManager.getModelData(GeometryManager.MISSING_IDENTIFIER);
        }
    }

    public static class Deserializer implements JsonDeserializer<GeometryData> {

        @Override
        public GeometryData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject data = json.getAsJsonObject();
            if(!data.get("format_version").getAsString().equals(FORMAT_VERSION))
                throw new JsonParseException("Invalid or missing Format Version for model");

            JsonObject geometry = data.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject();

            JsonObject desc = geometry.getAsJsonObject("description");
            int textureWidth = desc.get("texture_width").getAsInt();
            int textureHeight = desc.get("texture_height").getAsInt();

            JsonArray bonesJson = geometry.getAsJsonArray("bones");
            List<GeometryBone> bones = new ArrayList<>();
            bonesJson.forEach(e ->{
                GeometryBone bone = ClientInit.GSON_CLIENT.fromJson(e, GeometryBone.class);
                bone.setTextureSize(textureWidth, textureHeight);
                bones.add(bone);
            });

            return new GeometryData(textureWidth, textureHeight, bones);
        }
    }
}