package io.github.vampirestudios.obsidian;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

@Environment(EnvType.CLIENT)
public final class Animation {

    private final Identifier location;
    private final AnimatableModel<?> model;

    private int keyframeCount;
    private final List<Keyframe> keyframes = new ArrayList<>();

    public Animation(Identifier location, AnimatableModel<?> model) {
        this.location = location;
        this.model = model;
    }

    public void load() {
        ResourceManager rm = MinecraftClient.getInstance().getResourceManager();
        try(InputStream in = rm.getResource(location).getInputStream()) {
            JsonObject json = new JsonParser().parse(IOUtils.toString(in, Charset.defaultCharset())).getAsJsonObject();
            this.keyframeCount = json.get("framecount").getAsInt();
            json.get("data").getAsJsonArray().forEach(e -> {
                try {
                    Keyframe frame = Obsidian.GSON.fromJson(e, Keyframe.class);
                    frame.verifyData(this.model);
                    keyframes.add(frame);
                } catch(InvalidPropertiesFormatException ex) {
                    ex.printStackTrace();
                }
            });
            keyframes.sort(Comparator.comparingInt(Keyframe::getTimeStamp));
            fixAnimationData();
        }catch(IOException e ) {
            e.printStackTrace();
        }
    }

    private void fixAnimationData() {
        for (int i = 0; i < keyframeCount; i++) {
            Keyframe frame = keyframes.get(i);
            Map<ModelPart, Keyframe> lastApplicableRotation = new HashMap<>();
            Map<ModelPart, Keyframe> nextApplicableRotation = new HashMap<>();
            Map<ModelPart, Keyframe> lastApplicableTranslation = new HashMap<>();
            Map<ModelPart, Keyframe> nextApplicableTranslation = new HashMap<>();
            int l = i;
            frame.getData().forEach((mp, data) -> {
                if(l == 0) {
                    lastApplicableRotation.put(mp, keyframes.get(keyframeCount - 1));
                    lastApplicableTranslation.put(mp, keyframes.get(keyframeCount - 1));
                    nextApplicableRotation.put(mp, findNextApplicable(l, mp, false));
                    nextApplicableTranslation.put(mp, findNextApplicable(l, mp, true));
                    if(!data.hasRotation) {
                        data.pitch = mp.pitch; data.yaw = mp.yaw; data.roll = mp.roll;
                        data.hasRotation = true;
                    }
                    if(!data.hasTranslation)
                        data.hasTranslation = true;
                } else if(l == keyframeCount - 1) {
                    lastApplicableRotation.put(mp, findLastApplicable(l, mp, false));
                    lastApplicableTranslation.put(mp, findLastApplicable(l, mp, true));
                    nextApplicableRotation.put(mp, findNextApplicable(0, mp, false));
                    nextApplicableTranslation.put(mp, findNextApplicable(0, mp, true));
                    if(!data.hasRotation) {
                        data.pitch = mp.pitch; data.yaw = mp.yaw; data.roll = mp.roll;
                        data.hasRotation = true;
                    }
                    if(!data.hasTranslation)
                        data.hasTranslation = true;
                } else {
                    lastApplicableRotation.put(mp, findLastApplicable(l, mp, false));
                    lastApplicableTranslation.put(mp, findLastApplicable(l, mp, true));
                    nextApplicableRotation.put(mp, findNextApplicable(l, mp, false));
                    nextApplicableTranslation.put(mp, findNextApplicable(l, mp, true));
                }
            });
            frame.applicableFramePairRotation = new Pair<>(lastApplicableRotation, nextApplicableRotation);
            frame.applicableFramePairTranslation = new Pair<>(lastApplicableTranslation, nextApplicableTranslation);
        }
    }

    private Keyframe findLastApplicable(int index, ModelPart part, boolean translation) {
        List<Keyframe> previous = Lists.reverse(keyframes.subList(0, index + 1));
        Keyframe ret = previous.get(0);
        for (Keyframe keyframe : previous) {
            if (!translation && keyframe.getData().get(part).hasRotation) {
                ret = keyframe;
                break;
            } else if (translation && keyframe.getData().get(part).hasTranslation) {
                ret = keyframe;
                break;
            }
        }
        return ret;
    }

    private Keyframe findNextApplicable(int index, ModelPart part, boolean translation) {
        List<Keyframe> next = keyframes.subList(index + 1, keyframeCount);
        Keyframe ret = next.get(next.size() - 1);
        for (Keyframe keyframe : next) {
            if (!translation && keyframe.getData().get(part).hasRotation) {
                ret = keyframe;
                break;
            } else if (translation && keyframe.getData().get(part).hasTranslation) {
                ret = keyframe;
                break;
            }
        }
        return ret;
    }

    public void applyFrameData(Keyframe last, int tickcount, float delta) {
        Map<ModelPart, BoneFrameData> lastData = last.getData();

        lastData.forEach((mp, data) -> {
            Keyframe lastApplicable = last.applicableFramePairRotation.getLeft().get(mp);
            BoneFrameData lastApplicableData = lastApplicable.getData().get(mp);
            Keyframe nextApplicable = last.applicableFramePairRotation.getRight().get(mp);
            BoneFrameData nextApplicableData = nextApplicable.getData().get(mp);
            float interpolation = (tickcount - lastApplicable.getTimeStamp() + delta) / (nextApplicable.getTimeStamp() - lastApplicable.getTimeStamp());
            float pitch = MathHelper.lerpAngleDegrees(interpolation, lastApplicableData.pitch, nextApplicableData.pitch);
            float yaw = MathHelper.lerpAngleDegrees(interpolation, lastApplicableData.yaw, nextApplicableData.yaw);
            float roll = MathHelper.lerpAngleDegrees(interpolation, lastApplicableData.roll, nextApplicableData.roll);
            ((ModelPartExtension)(Object)mp).setRotation(new Vec3f(pitch, yaw, roll));

            lastApplicable = last.applicableFramePairTranslation.getLeft().get(mp);
            lastApplicableData = lastApplicable.getData().get(mp);
            nextApplicable = last.applicableFramePairTranslation.getRight().get(mp);
            nextApplicableData = nextApplicable.getData().get(mp);
            interpolation = (tickcount - lastApplicable.getTimeStamp() + delta) / (nextApplicable.getTimeStamp() - lastApplicable.getTimeStamp());
            float x = MathHelper.lerp(interpolation, lastApplicableData.transX, nextApplicableData.transX);
            float y = MathHelper.lerp(interpolation, lastApplicableData.transY, nextApplicableData.transY);
            float z = MathHelper.lerp(interpolation, lastApplicableData.transZ, nextApplicableData.transZ);
            //System.out.printf("X %f | Y %f | Z %f || X %f | Y %f | Z %f || Tick %d | Frame %d%n", x, y, z, pitch, yaw, roll, tickcount, last.getTimeStamp());
            ((ModelPartExtension)(Object)mp).setTranslation(new Vec3f(x, y, z));
        });
    }

    public List<Keyframe> getKeyframes() {
        return keyframes;
    }

    public int getLength() {
        return keyframes.get(keyframes.size() - 1).getTimeStamp();
    }

    public int getKeyframeCount() {
        return keyframeCount;
    }
}