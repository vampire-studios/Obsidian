package io.github.vampirestudios.obsidian;

import com.google.gson.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Pair;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;

public final class Keyframe {

    private final int timestamp;
    private final Map<String, BoneFrameData> rawData;
    private final Map<ModelPart, BoneFrameData> animationData;

    public Pair<Map<ModelPart, Keyframe>, Map<ModelPart, Keyframe>> applicableFramePairRotation;
    public Pair<Map<ModelPart, Keyframe>, Map<ModelPart, Keyframe>> applicableFramePairTranslation;

    private boolean verifiedData;

    public Keyframe(int timestamp, Map<String, BoneFrameData> data) {
        this.timestamp = timestamp;
        this.rawData = data;
        this.animationData = new HashMap<>();
        this.verifiedData = false;
    }

    public int getTimeStamp() {
        return timestamp;
    }

    @Environment(EnvType.CLIENT)
    public Map<ModelPart, BoneFrameData> getData() {
        return animationData;
    }

    @Environment(EnvType.CLIENT)
    public void verifyData(AnimatableModel<?> model) throws InvalidPropertiesFormatException {
        if(verifiedData)
            return;

        int result = 0;
        String boneIdError = "";

        for(Map.Entry<String, BoneFrameData> e : rawData.entrySet()) {
            ModelPart part = model.getAllParts().get(e.getKey());
            if(part == null) {
                result = 2;
                boneIdError = e.getKey();
                break;
            }
            animationData.put(part, e.getValue());
        }
        rawData.clear();

        switch(result) {
            case 2:
                throw new InvalidPropertiesFormatException("Frame referes to bones not present in model! [" + boneIdError + "]");
            case 0:
                this.verifiedData = true;
                return;
            default:
                throw new RuntimeException("Something unexpected happened while verifying animation data!");
        }
    }

    public static class Deserializer implements JsonDeserializer<Keyframe> {

        @Override
        public Keyframe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            int frame = obj.get("frame").getAsInt();
            Map<String, BoneFrameData> map = new HashMap<>();
            obj.get("bones").getAsJsonArray().forEach(e -> {
                JsonObject entry = e.getAsJsonObject();
                String bone = entry.get("bone").getAsString();
                BoneFrameData data = Obsidian.GSON.fromJson(entry.get("data").getAsJsonObject(), BoneFrameData.class);
                map.put(bone, data);
            });
            return new Keyframe(frame, map);
        }
    }
}