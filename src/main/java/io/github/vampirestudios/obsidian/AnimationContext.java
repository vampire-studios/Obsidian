package io.github.vampirestudios.obsidian;

import com.google.common.collect.Lists;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.utils.Tuple;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationContext {

    public final Identifier id;
    private final AnimationData animation;

    private final Map<Tuple<String, Transformation>, Vec3f> prevTransform = new HashMap<>();
    private final Map<Tuple<String, Transformation>, List<Double>> keyframes = new HashMap<>();
    private final Map<Tuple<String, Transformation>, double[]> currentTimestamp = new HashMap<>();

    private boolean isPaused = false, isFinished = false;

    public AnimationContext(Identifier id) {
        this.id = id;
        this.animation = ClientInit.INSTANCE.animationManager.getAnimationData(id);
        populateStartData(true);
    }

    public void applyData(GeometryModel model, int animationAge, float tickDelta) {
        if(isPaused || isDone())
            return;

        float time = animationAge + tickDelta;

        for(Map.Entry<String, AnimationBone> entry : animation.getData().entrySet()) {
            AnimationBone bone = entry.getValue();
            String id = entry.getKey();

            if(bone.hasRotation) {
                Vec3f target = applyTransformation(bone, id, Transformation.ROTATION, time, tickDelta);
                model.getModelData().getBone(id).addRotation(target.getX(), target.getY(), target.getZ(), true);
            }

            if(bone.hasTranslation) {
                Vec3f target = applyTransformation(bone, id, Transformation.TRANSLATION, time, tickDelta);
                model.getModelData().getBone(id).addTranslation(target.getX(), target.getY(), target.getZ());
            }

            if(bone.hasScale) {
                Vec3f target = applyTransformation(bone, id, Transformation.SCALE, time, tickDelta);
                model.getModelData().getBone(id).addScaling(target.getX(), target.getY(), target.getZ());
            }
        }

        if(animationAge > animation.length * 20)
            this.isFinished = true;
    }

    public void loop() {
        this.isFinished = false;
        populateStartData(false);
    }

    public boolean shouldLoop() {
        return animation.loop && isFinished;
    }

    public boolean isDone() {
        return !animation.loop && isFinished;
    }

    private Vec3f applyTransformation(AnimationBone bone, String id, Transformation type, float time, float tickDelta) {
        Tuple<String, Transformation> key = new Tuple<>(id, type);

        double timestamp0 = currentTimestamp.get(key)[0];
        double timestamp1 = currentTimestamp.get(key)[1];

        if(time >= (timestamp1 * 20))
            currentTimestamp.replace(key, getKeyframesTimestamps(keyframes.get(key), time));

        if(!bone.hasDataForFrame(timestamp0, type) || !bone.hasDataForFrame(timestamp1, type))
            return new Vec3f();

        float startTime = (float)timestamp0 * 20;
        float endTime = (float)(timestamp1 * 20) - startTime;

        float progressDelta = (float)Math.min((time - startTime) / endTime, 1.0);

        Vec3f minPeriod = bone.getDataForFrame(timestamp0, type);
        Vec3f maxPeriod = bone.getDataForFrame(timestamp1, type);
        Vec3f previous = prevTransform.get(key);

        Vec3f target, transform;

        if(type == Transformation.ROTATION) {
            transform = new Vec3f(
                    MathHelper.lerpAngleDegrees(progressDelta, minPeriod.getX(), maxPeriod.getX()),
                    MathHelper.lerpAngleDegrees(progressDelta, minPeriod.getY(), maxPeriod.getY()),
                    MathHelper.lerpAngleDegrees(progressDelta, minPeriod.getZ(), maxPeriod.getZ()));
            target = new Vec3f(
                    MathHelper.lerpAngleDegrees(tickDelta, previous.getX(), transform.getX()),
                    MathHelper.lerpAngleDegrees(tickDelta, previous.getY(), transform.getY()),
                    MathHelper.lerpAngleDegrees(tickDelta, previous.getZ(), transform.getZ()));
        } else {
            transform = new Vec3f(
                    MathHelper.lerp(progressDelta, minPeriod.getX(), maxPeriod.getX()),
                    MathHelper.lerp(progressDelta, minPeriod.getY(), maxPeriod.getY()),
                    MathHelper.lerp(progressDelta, minPeriod.getZ(), maxPeriod.getZ()));
            target = new Vec3f(
                    MathHelper.lerp(tickDelta, previous.getX(), transform.getX()),
                    MathHelper.lerp(tickDelta, previous.getY(), transform.getY()),
                    MathHelper.lerp(tickDelta, previous.getZ(), transform.getZ()));
        }

        prevTransform.replace(key, transform);
        if(type == Transformation.TRANSLATION)
            System.out.printf("TimeStamps: %f/%f/%f | Delta: %f | Min: %s | Max: %s | Current: %s%n", currentTimestamp.get(key)[0] * 20, currentTimestamp.get(key)[1] * 20, time, progressDelta, minPeriod.toString(), maxPeriod.toString(), transform.toString());

        return target;
    }

    private void setStartData(String id, AnimationBone b, Transformation type, boolean full) {
        Tuple<String, Transformation> key = new Tuple<>(id, type);
        if(full) {
            List<Double> values = Lists.newArrayList(b.rotationData.keySet());
            values.sort(Double::compare);
            keyframes.put(key, values);
        }
        currentTimestamp.put(key, getKeyframesTimestamps(keyframes.get(key), 0));
        prevTransform.put(key, b.hasDataForFrame(0.0, type) ? b.getDataForFrame(0.0, type) : new Vec3f(0, 0 ,0));

    }

    private void populateStartData(boolean full) {
        animation.getData().forEach((id, b) -> {
            if(b.hasRotation)
                setStartData(id, b, Transformation.ROTATION, full);
            if(b.hasTranslation)
                setStartData(id, b, Transformation.TRANSLATION, full);
            if(b.hasScale)
                setStartData(id, b, Transformation.SCALE, full);
        });
    }

    private double[] getKeyframesTimestamps(List<Double> keyframes, float animationAge) {
        double start = 0.0D, end = keyframes.get(keyframes.size() - 1);
        for(double d : Lists.reverse(keyframes)) {
            double adjusted = d * 20;
            if(adjusted > animationAge)
                end = d;
            if(adjusted <= animationAge) {
                start = d;
                break;
            }
        }

        return new double[] { start, end };
    }
} 