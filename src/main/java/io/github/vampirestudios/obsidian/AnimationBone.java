package io.github.vampirestudios.obsidian;

import com.google.gson.*;
import net.minecraft.client.util.math.Vector3f;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AnimationBone {

    public Map<Double, Vector3f> rotationData, translationData, scaleData;
    public boolean hasRotation, hasTranslation, hasScale = false;

    public boolean hasDataForFrame(double timestamp, Transformation type) {
        switch(type) {
            case ROTATION:
                return hasRotation && rotationData.containsKey(timestamp);
            case TRANSLATION:
                return hasTranslation && translationData.containsKey(timestamp);
            case SCALE:
                return hasScale && scaleData.containsKey(timestamp);
        }
        return false;
    }

    public Vector3f getDataForFrame(double timestamp, Transformation type) {
        switch(type) {
            case ROTATION:
                return rotationData.get(timestamp);
            case TRANSLATION:
                return translationData.get(timestamp);
            case SCALE:
                return scaleData.get(timestamp);
        }
        return new Vector3f();
    }


    public void setRotation(Map<Double, Vector3f> data) {
        this.hasRotation = true;
        this.rotationData = data;
    }

    public void setTranslation(Map<Double, Vector3f> data) {
        this.hasTranslation = true;
        this.translationData = data;
    }

    public void setScale(Map<Double, Vector3f> data) {
        this.hasScale = true;
        this.scaleData = data;
    }

    public static class Deserializer implements JsonDeserializer<AnimationBone> {

        @Override
        public AnimationBone deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            AnimationBone bone = new AnimationBone();

            if(obj.has("rotation")) {
                Map<Double, Vector3f> data = new HashMap<>();
                obj.get("rotation").getAsJsonObject().entrySet().forEach(e -> {
                    double timestamp = Double.parseDouble(e.getKey());
                    JsonArray array = e.getValue().getAsJsonArray();
                    data.put(timestamp, new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()));
                });
                bone.setRotation(data);
            }

            if(obj.has("position")) {
                Map<Double, Vector3f> data = new HashMap<>();
                obj.get("position").getAsJsonObject().entrySet().forEach(e -> {
                    double timestamp = Double.parseDouble(e.getKey());
                    JsonArray array = e.getValue().getAsJsonArray();
                    data.put(timestamp, new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()));
                });
                bone.setTranslation(data);
            }

            if(obj.has("scale")) {
                Map<Double, Vector3f> data = new HashMap<>();
                obj.get("scale").getAsJsonObject().entrySet().forEach(e -> {
                    double timestamp = Double.parseDouble(e.getKey());
                    JsonArray array = e.getValue().getAsJsonArray();
                    data.put(timestamp, new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()));
                });
                bone.setScale(data);
            }

            return bone;
        }
    }
}