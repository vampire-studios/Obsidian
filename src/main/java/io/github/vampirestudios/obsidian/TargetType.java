package io.github.vampirestudios.obsidian;

import com.mojang.serialization.Codec;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.util.StringRepresentable;
import org.joml.Vector3f;

import java.util.function.Function;

public enum TargetType implements StringRepresentable {
    POSITION("towards", AnimationChannel.Targets.POSITION, vector3f -> KeyframeAnimations.posVec(vector3f.x, vector3f.y, vector3f.z)),
    ROTATION("rotation", AnimationChannel.Targets.ROTATION, vector3f -> KeyframeAnimations.degreeVec(vector3f.x, vector3f.y, vector3f.z)),
    SCALE("scale", AnimationChannel.Targets.SCALE, vector3f -> KeyframeAnimations.scaleVec(vector3f.x, vector3f.y, vector3f.z));

    public static final Codec<TargetType> CODEC = StringRepresentable.fromEnum(TargetType::values);

    private final String name;
    private final AnimationChannel.Target target;
    private final Function<Vector3f, Vector3f> transformedTarget;

    TargetType(String name, AnimationChannel.Target target, Function<Vector3f, Vector3f> targetVector) {
        this.name = name;
        this.target = target;
        this.transformedTarget = targetVector;
    }

    public AnimationChannel.Target getTarget() {
        return this.target;
    }

    public Vector3f transformTarget(Vector3f target) {
        return this.transformedTarget.apply(target);
    }

    public String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}