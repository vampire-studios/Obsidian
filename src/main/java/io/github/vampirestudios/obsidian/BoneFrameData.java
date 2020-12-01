package io.github.vampirestudios.obsidian;

import com.google.gson.*;

import java.lang.reflect.Type;

public final class BoneFrameData {

    public float pitch, yaw, roll;
    public float transX, transY, transZ;
    public float scaleX, scaleY, scaleZ;
    public boolean hasRotation, hasTranslation, hasScaling;

    public BoneFrameData() {
        this.pitch = 0; this.yaw = 0; this.roll = 0;
        this.transX = 0; this.transY = 0; this.transZ = 0;
        this.scaleX = 1; this.scaleY = 1; this.scaleZ = 1;
        this.hasRotation = false; this.hasTranslation = false; this.hasScaling = false;
    }

    public static final class Deserializer implements JsonDeserializer<BoneFrameData> {

        @Override
        public BoneFrameData deserialize(JsonElement json, Type t, JsonDeserializationContext ctx) throws JsonParseException {
            BoneFrameData frame = new BoneFrameData();
            JsonObject data = json.getAsJsonObject();
            if(!data.isJsonNull()) {
                if(data.has("rotation")) {
                    JsonObject rotation = data.get("rotation").getAsJsonObject();
                    frame.pitch = rotation.get("pitch").getAsFloat(); frame.yaw = rotation.get("yaw").getAsFloat(); frame.roll = rotation.get("roll").getAsFloat();
                    frame.hasRotation = true;
                }
                if(data.has("translation")) {
                    JsonObject translation = data.get("translation").getAsJsonObject();
                    frame.transX = translation.get("x").getAsFloat(); frame.transY = translation.get("y").getAsFloat(); frame.transZ = translation.get("z").getAsFloat();
                    frame.hasTranslation = true;
                }
                if(data.has("scaling")) {
                    JsonObject scaling = data.get("scaling").getAsJsonObject();
                    frame.scaleX = scaling.get("x").getAsFloat(); frame.scaleY = scaling.get("y").getAsFloat(); frame.scaleZ = scaling.get("z").getAsFloat();
                    frame.hasScaling = true;
                }
            }
            return frame;
        }
    }
}