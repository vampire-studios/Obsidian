package io.github.vampirestudios.obsidian.animation;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.obsidian.mixins.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

import java.util.*;

public class Codecs {
    private static final Codec<Vector3f> VECTOR3F_CODEC = Codec.FLOAT.listOf().comapFlatMap(
            coordinates -> Util.toArray(coordinates, 3)
                    .map(coords -> new Vector3f(coords.get(0), coords.get(1),
                            coords.get(2))),
            vec -> List.of(vec.x, vec.y, vec.z)
    );
    public static final class Animations {
        public static final Codec<Keyframe> KEYFRAME = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("timestamp").forGetter(Keyframe::comp_600),
                net.minecraft.util.dynamic.Codecs.VECTOR_3F.fieldOf("transformation").forGetter(Keyframe::comp_601),
                Codec.STRING.flatXmap(
                        s -> AnimationUtils.getInterpolatorFromName(s).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown interpolator: " + s)),
                        i -> AnimationUtils.getNameForInterpolator(i).map(DataResult::success).orElse(DataResult.error(() -> "Unknown interpolator"))
                ).fieldOf("interpolator").forGetter(Keyframe::comp_602)
        ).apply(instance, Keyframe::new));

        public static final Codec<Transformation> PART_ANIMATION = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.flatXmap(s -> switch (s) {
                    case "TRANSLATE" -> DataResult.success(Transformation.Targets.TRANSLATE);
                    case "ROTATE" -> DataResult.success(Transformation.Targets.ROTATE);
                    case "SCALE" -> DataResult.success(Transformation.Targets.SCALE);
                    default -> DataResult.error(() -> "Unknown transformation: " + s);
                }, transformation -> {
                    if (transformation == Transformation.Targets.TRANSLATE) {
                        return DataResult.success("TRANSLATE");
                    } else if (transformation == Transformation.Targets.ROTATE) {
                        return DataResult.success("ROTATE");
                    } else if (transformation == Transformation.Targets.SCALE) {
                        return DataResult.success("SCALE");
                    } else {
                        return DataResult.error(() -> "Unknown transformation");
                    }
                }).fieldOf("transformation").forGetter(Transformation::comp_595),
                Codec.list(KEYFRAME).xmap(list -> list.toArray(Keyframe[]::new), Arrays::asList).fieldOf("keyframes").forGetter(Transformation::comp_596)
        ).apply(instance, Transformation::new));

        public static final Codec<Animation> ANIMATION = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("length").forGetter(Animation::comp_597),
                Codec.BOOL.fieldOf("looping").forGetter(Animation::comp_598),
                Codec.unboundedMap(Codec.STRING, Codec.list(PART_ANIMATION)).fieldOf("animations").forGetter(Animation::comp_599)
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
                        VECTOR3F_CODEC.optionalFieldOf("origin", new Vector3f()).forGetter(obj -> new Vector3f(obj.pivotX, obj.pivotY, obj.pivotZ)),
                        VECTOR3F_CODEC.optionalFieldOf("rotation", new Vector3f()).forGetter(obj -> new Vector3f(obj.pitch, obj.yaw, obj.roll))
                ).apply(instance, (origin, rot) -> ModelTransform.of(origin.x(), origin.y(), origin.z(), rot.x(), rot.y(), rot.z()))
        );

        public static final Codec<Dilation> DILATION = VECTOR3F_CODEC.xmap(
                vec -> new Dilation(vec.x(), vec.y(), vec.z()),
                dil -> new Vector3f(
                        ((DilationAccessor) dil).getRadiusX(),
                        ((DilationAccessor) dil).getRadiusY(),
                        ((DilationAccessor) dil).getRadiusZ()
                )
        );

        public static final Codec<Vector2f> VECTOR2F = Codec.FLOAT.listOf().comapFlatMap((vec) ->
                        Util.toArray(vec, 2).map((arr) -> new Vector2f(arr.get(0), arr.get(1))),
                (vec) -> ImmutableList.of(vec.getX(), vec.getY())
        );

        private static ModelCuboidData createCuboidData(Optional<String> name, Vector3f offset, Vector3f dimensions, Dilation dilation, boolean mirror, Vector2f uv, Vector2f uvSize, Set<Direction> directionSet) {
            return ModelCuboidDataAccessor.createModelCuboidData(name.orElse(null), uv.getX(), uv.getY(), offset.x(), offset.y(), offset.z(), dimensions.x(), dimensions.y(), dimensions.z(), dilation, mirror, uvSize.getX(), uvSize.getY(), directionSet);
        }

        private static final Vector2f DEFAULT_UV_SCALE = new Vector2f(1.0f, 1.0f);

        public static final Codec<ModelCuboidData> MODEL_CUBOID_DATA = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Codec.STRING.optionalFieldOf("name").forGetter(obj -> Optional.ofNullable(((ModelCuboidDataAccessor) (Object) obj).getName())),
                        VECTOR3F_CODEC.fieldOf("offset").forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getOffset()),
                        VECTOR3F_CODEC.fieldOf("dimensions").forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getDimensions()),
                        DILATION.optionalFieldOf("dilation", Dilation.NONE).forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getExtraSize()),
                        Codec.BOOL.optionalFieldOf("mirror", false).forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).isMirror()),
                        VECTOR2F.fieldOf("uv").forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getTextureUV()),
                        VECTOR2F.optionalFieldOf("uv_scale", DEFAULT_UV_SCALE).forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getTextureScale()),
                        Direction.CODEC.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("directions").forGetter(modelCuboidData -> ((ModelCuboidDataAccessor) (Object) modelCuboidData).getDirections())
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