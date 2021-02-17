package io.github.vampirestudios.obsidian;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import io.github.vampirestudios.obsidian.client.ClientInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AnimationData {

    public static final String FORMAT_VERSION = "1.8.0";
    public boolean shouldReset, loop = false;
    private final Map<String, AnimationBone> data;
    public final double length;

    public AnimationData(Map<String, AnimationBone> data, double length) {
        this.data = data;
        this.length = length;
    }

    public Map<String, AnimationBone> getData() {
        return data;
    }

    //TODO USE INTERNAL RESOURCE MANAGER
    public static AnimationData load(ResourceManager resourceManager, Identifier identifier) {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(identifier);

            AnimationData data;
            try(JsonReader reader = ClientInit.GSON_CLIENT.newJsonReader(new InputStreamReader(resource.getInputStream()))) {
                data = ClientInit.GSON_CLIENT.fromJson(reader, AnimationData.class);
            } catch(JsonParseException e) {
                System.out.println("Failed to parse model json!");
                e.printStackTrace();
                return ClientInit.INSTANCE.animationManager.getAnimationData(AnimationManager.MISSING_IDENTIFIER);
            }

            return data;
        } catch (IOException ex) {
            ex.printStackTrace();
            return ClientInit.INSTANCE.animationManager.getAnimationData(AnimationManager.MISSING_IDENTIFIER);
        }
    }

    public static class Deserializer implements JsonDeserializer<AnimationData> {

        @Override
        public AnimationData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = ((Map.Entry<String, JsonElement>) json.getAsJsonObject().get("animations").getAsJsonObject().entrySet().toArray()[0]).getValue().getAsJsonObject();
            double length = obj.get("animation_length").getAsDouble();
            Map<String, AnimationBone> data = new HashMap<>();
            obj.get("bones").getAsJsonObject().entrySet().forEach(e -> {
                AnimationBone bone = ClientInit.GSON_CLIENT.fromJson(e.getValue(), AnimationBone.class);
                data.put(e.getKey(), bone);
            });
            AnimationData value = new AnimationData(data, length);
            value.loop = obj.has("loop") && obj.get("loop").getAsBoolean();
            value.shouldReset = obj.has("override_previous_animation") && obj.get("override_previous_animation").getAsBoolean();

            return value;
        }

    }

}
