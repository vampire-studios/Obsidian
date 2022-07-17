package io.github.vampirestudios.obsidian.animation;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.obsidian.mixins.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.animation.Animation;
import net.minecraft.client.render.animation.AnimationKeyframe;
import net.minecraft.client.render.animation.PartAnimation;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class Codecs {
    public static final class Animations {
        public static final Codec<AnimationKeyframe> KEYFRAME = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("timestamp").forGetter(AnimationKeyframe::timestamp),
                Vec3f.CODEC.fieldOf("transformation").forGetter(AnimationKeyframe::transformation),
                Codec.STRING.flatXmap(
                        s -> AnimationUtils.getInterpolatorFromName(s).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown interpolator: " + s)),
                        i -> AnimationUtils.getNameForInterpolator(i).map(DataResult::success).orElse(DataResult.error("Unknown interpolator"))
                ).fieldOf("interpolator").forGetter(AnimationKeyframe::interpolator)
        ).apply(instance, AnimationKeyframe::new));

        public static final Codec<PartAnimation> PART_ANIMATION = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.flatXmap(s -> switch (s) {
                    case "TRANSLATE" -> DataResult.success(PartAnimation.AnimationTargets.TRANSLATE);
                    case "ROTATE" -> DataResult.success(PartAnimation.AnimationTargets.ROTATE);
                    case "SCALE" -> DataResult.success(PartAnimation.AnimationTargets.SCALE);
                    default -> DataResult.error("Unknown transformation: " + s);
                }, transformation -> {
                    if (transformation == PartAnimation.AnimationTargets.TRANSLATE) {
                        return DataResult.success("TRANSLATE");
                    } else if (transformation == PartAnimation.AnimationTargets.ROTATE) {
                        return DataResult.success("ROTATE");
                    } else if (transformation == PartAnimation.AnimationTargets.SCALE) {
                        return DataResult.success("SCALE");
                    } else {
                        return DataResult.error("Unknown transformation");
                    }
                }).fieldOf("transformation").forGetter(PartAnimation::transformation),
                Codec.list(KEYFRAME).xmap(list -> list.toArray(AnimationKeyframe[]::new), Arrays::asList).fieldOf("keyframes").forGetter(PartAnimation::keyframes)
        ).apply(instance, PartAnimation::new));

        public static final Codec<Animation> ANIMATION = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("length").forGetter(Animation::length),
                Codec.BOOL.fieldOf("looping").forGetter(Animation::looping),
                Codec.unboundedMap(Codec.STRING, Codec.list(PART_ANIMATION)).fieldOf("animations").forGetter(Animation::animations)
        ).apply(instance, Animation::new));
    }

    public static final class Model {
        public static final Codec<TextureDimensions> TEXTURE_DIMENSIONS = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Codec.INT.fieldOf("width").forGetter(obj -> ((TextureDimensionsAccessor) obj).getWidth()),
                        Codec.INT.fieldOf("height").forGetter(obj -> ((TextureDimensionsAccessor) obj).getHeight())
                ).apply(instance, TextureDimensions::new)
        );

        public static final Codec<ModelTransform> MODEL_TRANSFORM = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Vec3f.CODEC.optionalFieldOf("origin", Vec3f.ZERO).forGetter(obj -> new Vec3f(obj.pivotX, obj.pivotY, obj.pivotZ)),
                        Vec3f.CODEC.optionalFieldOf("rotation", Vec3f.ZERO).forGetter(obj -> new Vec3f(obj.pitch, obj.yaw, obj.roll))
                ).apply(instance, (origin, rot) -> ModelTransform.of(origin.getX(), origin.getY(), origin.getZ(), rot.getX(), rot.getY(), rot.getZ()))
        );

        public static final Codec<Dilation> DILATION = Vec3f.CODEC.xmap(
                vec -> new Dilation(vec.getX(), vec.getY(), vec.getZ()),
                dil -> new Vec3f(
                        ((DilationAccessor) dil).getRadiusX(),
                        ((DilationAccessor) dil).getRadiusY(),
                        ((DilationAccessor) dil).getRadiusZ())
        );

        public static final Codec<Vector2f> VECTOR2F = Codec.FLOAT.listOf().comapFlatMap((vec) ->
                        Util.fixedSizeList(vec, 2).map((arr) -> new Vector2f(arr.get(0), arr.get(1))),
                (vec) -> ImmutableList.of(vec.getX(), vec.getY())
        );

        private static ModelCuboidData createCuboidData(Optional<String> name, Vec3f offset, Vec3f dimensions, Dilation dilation, boolean mirror, Vector2f uv, Vector2f uvSize) {
            return ModelCuboidDataAccessor.create(name.orElse(null), uv.getX(), uv.getY(), offset.getX(), offset.getY(), offset.getZ(), dimensions.getX(), dimensions.getY(), dimensions.getZ(), dilation, mirror, uvSize.getX(), uvSize.getY());
        }

        private static final Vector2f DEFAULT_UV_SCALE = new Vector2f(1.0f, 1.0f);

        public static final Codec<ModelCuboidData> MODEL_CUBOID_DATA = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Codec.STRING.optionalFieldOf("name").forGetter(obj -> Optional.ofNullable(((ModelCuboidDataAccessor) (Object) obj).getName())),
                        Vec3f.CODEC.fieldOf("offset").forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getOffset()),
                        Vec3f.CODEC.fieldOf("dimensions").forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getDimensions()),
                        DILATION.optionalFieldOf("dilation", Dilation.NONE).forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getExtraSize()),
                        Codec.BOOL.optionalFieldOf("mirror", false).forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).isMirror()),
                        VECTOR2F.fieldOf("uv").forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getTextureUV()),
                        VECTOR2F.optionalFieldOf("uv_scale", DEFAULT_UV_SCALE).forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getTextureScale())
                ).apply(instance, Model::createCuboidData)
        );

        public static final Codec<ModelPartData> MODEL_PART_DATA = RecordCodecBuilder.create((instance) ->
                instance.group(
                        MODEL_TRANSFORM.optionalFieldOf("transform", ModelTransform.NONE).forGetter(obj -> ((ModelPartDataAccessor) obj).getRotationData()),
                        Codec.list(MODEL_CUBOID_DATA).fieldOf("cuboids").forGetter(obj -> ((ModelPartDataAccessor) obj).getCuboidData()),
                        Codec.unboundedMap(Codec.STRING, Model.MODEL_PART_DATA).optionalFieldOf("children", new HashMap<>()).forGetter(obj -> ((ModelPartDataAccessor) obj).getChildren())
                ).apply(instance, (transform, cuboids, children) -> {
                    var data = ModelPartDataAccessor.create(cuboids, transform);
                    ((ModelPartDataAccessor) data).getChildren().putAll(children);
                    return data;
                })
        );

        public static final Codec<TexturedModelData> TEXTURED_MODEL_DATA = RecordCodecBuilder.create((instance) ->
                instance.group(
                        TEXTURE_DIMENSIONS.fieldOf("texture").forGetter(obj -> ((TexturedModelDataAccessor) obj).getDimensions()),
                        Codec.unboundedMap(Codec.STRING, MODEL_PART_DATA).fieldOf("bones").forGetter(obj -> ((ModelPartDataAccessor) ((TexturedModelDataAccessor) obj).getData().getRoot()).getChildren())
                ).apply(instance, (texture, bones) -> {
                    var data = new ModelData();
                    ((ModelPartDataAccessor) data.getRoot()).getChildren().putAll(bones);
                    return TexturedModelDataAccessor.create(data, texture);
                })
        );
    }
}