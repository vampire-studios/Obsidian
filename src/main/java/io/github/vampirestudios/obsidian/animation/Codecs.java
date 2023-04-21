package io.github.vampirestudios.obsidian.animation;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.obsidian.mixins.*;
import net.minecraft.Util;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.*;

public class Codecs {
    private static final Codec<Vector3f> VECTOR3F_CODEC = Codec.FLOAT.listOf().comapFlatMap(
            coordinates -> Util.fixedSize(coordinates, 3)
                    .map(coords -> new Vector3f(coords.get(0), coords.get(1),
                            coords.get(2))),
            vec -> List.of(vec.x, vec.y, vec.z)
    );
    public static final class Animations {
        public static final Codec<Keyframe> KEYFRAME = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("timestamp").forGetter(Keyframe::timestamp),
                net.minecraft.util.ExtraCodecs.VECTOR3F.fieldOf("transformation").forGetter(Keyframe::target),
                Codec.STRING.flatXmap(
                        s -> AnimationUtils.getInterpolatorFromName(s).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown interpolator: " + s)),
                        i -> AnimationUtils.getNameForInterpolator(i).map(DataResult::success).orElse(DataResult.error(() -> "Unknown interpolator"))
                ).fieldOf("interpolator").forGetter(Keyframe::interpolation)
        ).apply(instance, Keyframe::new));

        public static final Codec<AnimationChannel> PART_ANIMATION = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.flatXmap(s -> switch (s) {
                    case "TRANSLATE" -> DataResult.success(AnimationChannel.Targets.POSITION);
                    case "ROTATE" -> DataResult.success(AnimationChannel.Targets.ROTATION);
                    case "SCALE" -> DataResult.success(AnimationChannel.Targets.SCALE);
                    default -> DataResult.error(() -> "Unknown transformation: " + s);
                }, transformation -> {
                    if (transformation == AnimationChannel.Targets.POSITION) {
                        return DataResult.success("TRANSLATE");
                    } else if (transformation == AnimationChannel.Targets.ROTATION) {
                        return DataResult.success("ROTATE");
                    } else if (transformation == AnimationChannel.Targets.SCALE) {
                        return DataResult.success("SCALE");
                    } else {
                        return DataResult.error(() -> "Unknown transformation");
                    }
                }).fieldOf("transformation").forGetter(AnimationChannel::target),
                Codec.list(KEYFRAME).xmap(list -> list.toArray(Keyframe[]::new), Arrays::asList).fieldOf("keyframes").forGetter(AnimationChannel::keyframes)
        ).apply(instance, AnimationChannel::new));

        public static final Codec<AnimationDefinition> ANIMATION = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("length").forGetter(AnimationDefinition::lengthInSeconds),
                Codec.BOOL.fieldOf("looping").forGetter(AnimationDefinition::looping),
                Codec.unboundedMap(Codec.STRING, Codec.list(PART_ANIMATION)).fieldOf("animations").forGetter(AnimationDefinition::boneAnimations)
        ).apply(instance, AnimationDefinition::new));
    }

    public static final class Model {
        public static final Codec<MaterialDefinition> TEXTURE_DIMENSIONS = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Codec.INT.fieldOf("width").forGetter(obj -> ((TextureDimensionsAccessor) obj).getWidth()),
                        Codec.INT.fieldOf("height").forGetter(obj -> ((TextureDimensionsAccessor) obj).getHeight())
                ).apply(instance, MaterialDefinition::new)
        );

        public static final Codec<PartPose> MODEL_TRANSFORM = RecordCodecBuilder.create((instance) ->
                instance.group(
                        VECTOR3F_CODEC.optionalFieldOf("origin", new Vector3f()).forGetter(obj -> new Vector3f(obj.x, obj.y, obj.z)),
                        VECTOR3F_CODEC.optionalFieldOf("rotation", new Vector3f()).forGetter(obj -> new Vector3f(obj.xRot, obj.yRot, obj.zRot))
                ).apply(instance, (origin, rot) -> PartPose.offsetAndRotation(origin.x(), origin.y(), origin.z(), rot.x(), rot.y(), rot.z()))
        );

        public static final Codec<CubeDeformation> DILATION = VECTOR3F_CODEC.xmap(
                vec -> new CubeDeformation(vec.x(), vec.y(), vec.z()),
                dil -> new Vector3f(
                        ((DilationAccessor) dil).getRadiusX(),
                        ((DilationAccessor) dil).getRadiusY(),
                        ((DilationAccessor) dil).getRadiusZ()
                )
        );

        public static final Codec<UVPair> VECTOR2F = Codec.FLOAT.listOf().comapFlatMap((vec) ->
                        Util.fixedSize(vec, 2).map((arr) -> new UVPair(arr.get(0), arr.get(1))),
                (vec) -> ImmutableList.of(vec.u(), vec.v())
        );

        private static CubeDefinition createCuboidData(Optional<String> name, Vector3f offset, Vector3f dimensions, CubeDeformation dilation, boolean mirror, UVPair uv, UVPair uvSize, Set<Direction> directionSet) {
            return ModelCuboidDataAccessor.createModelCuboidData(name.orElse(null), uv.u(), uv.v(), offset.x(), offset.y(), offset.z(), dimensions.x(), dimensions.y(), dimensions.z(), dilation, mirror, uvSize.u(), uvSize.v(), directionSet);
        }

        private static final UVPair DEFAULT_UV_SCALE = new UVPair(1.0f, 1.0f);

        public static final Codec<CubeDefinition> MODEL_CUBOID_DATA = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Codec.STRING.optionalFieldOf("name").forGetter(obj -> Optional.ofNullable(((ModelCuboidDataAccessor) (Object) obj).getName())),
                        VECTOR3F_CODEC.fieldOf("offset").forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getOffset()),
                        VECTOR3F_CODEC.fieldOf("dimensions").forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getDimensions()),
                        DILATION.optionalFieldOf("dilation", CubeDeformation.NONE).forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getExtraSize()),
                        Codec.BOOL.optionalFieldOf("mirror", false).forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).isMirror()),
                        VECTOR2F.fieldOf("uv").forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getTextureUV()),
                        VECTOR2F.optionalFieldOf("uv_scale", DEFAULT_UV_SCALE).forGetter(obj -> ((ModelCuboidDataAccessor) (Object) obj).getTextureScale()),
                        Direction.CODEC.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("directions").forGetter(modelCuboidData -> ((ModelCuboidDataAccessor) (Object) modelCuboidData).getDirections())
                ).apply(instance, Model::createCuboidData)
        );

        public static final Codec<PartDefinition> MODEL_PART_DATA = RecordCodecBuilder.create((instance) ->
                instance.group(
                        MODEL_TRANSFORM.optionalFieldOf("transform", PartPose.ZERO).forGetter(obj -> ((ModelPartDataAccessor) obj).getRotationData()),
                        Codec.list(MODEL_CUBOID_DATA).fieldOf("cuboids").forGetter(obj -> ((ModelPartDataAccessor) obj).getCuboidData()),
                        Codec.unboundedMap(Codec.STRING, Model.MODEL_PART_DATA).optionalFieldOf("children", new HashMap<>()).forGetter(obj -> ((ModelPartDataAccessor) obj).getChildren())
                ).apply(instance, (transform, cuboids, children) -> {
                    var data = ModelPartDataAccessor.create(cuboids, transform);
                    ((ModelPartDataAccessor) data).getChildren().putAll(children);
                    return data;
                })
        );

        public static final Codec<LayerDefinition> TEXTURED_MODEL_DATA = RecordCodecBuilder.create((instance) ->
                instance.group(
                        TEXTURE_DIMENSIONS.fieldOf("texture").forGetter(obj -> ((TexturedModelDataAccessor) obj).getDimensions()),
                        Codec.unboundedMap(Codec.STRING, MODEL_PART_DATA).fieldOf("bones").forGetter(obj -> ((ModelPartDataAccessor) ((TexturedModelDataAccessor) obj).getData().getRoot()).getChildren())
                ).apply(instance, (texture, bones) -> {
                    var data = new MeshDefinition();
                    ((ModelPartDataAccessor) data.getRoot()).getChildren().putAll(bones);
                    return TexturedModelDataAccessor.create(data, texture);
                })
        );
    }
}