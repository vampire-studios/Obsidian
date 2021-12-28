package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.utils.Vec2i;
import net.minecraft.client.model.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import java.util.List;

public class ArmorModel {

    public Identifier name;
    public Bone[] bones;
    public Vec2i textureSize = Vec2i.ZERO.copy();

    public static class Bone {

        public String name = "";
        public String parent = null;
        public Vec3f pivot = Vec3f.ZERO.copy();
        public Vec3f rotation = Vec3f.ZERO.copy();
        public List<Cube> cubes;
        public boolean mirrored = false;

        public static class Cube {
            public String name = "";
            public boolean mirrored = false;
            public Vec2i uv = Vec2i.ZERO.copy();
            public Vec3f offset = Vec3f.ZERO.copy();
            public Vec3f size = Vec3f.ZERO.copy();
            public Vec3f pivot = Vec3f.ZERO.copy();
            public Vec3f radiusArray = Vec3f.ZERO.copy();
            public float radius;
        }

    }

    public TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        for (ArmorModel.Bone part : bones) {
            ModelPartBuilder modelPartBuilder = ModelPartBuilder.create();
            for (ArmorModel.Bone.Cube cube : part.cubes) {
                modelPartBuilder.uv(cube.uv.x(), cube.uv.y());

                Dilation dilation = Dilation.NONE;
                if(cube.radiusArray != null) dilation.add(cube.radiusArray.getX(), cube.radiusArray.getY(), cube.radiusArray.getZ());
                else if(cube.radius != 0) dilation.add(cube.radius);

                if (cube.name.isEmpty()) modelPartBuilder.cuboid(cube.offset.getX(), cube.offset.getY(), cube.offset.getZ(), cube.size.getX(), cube.size.getY(), cube.size.getZ(), dilation);
                else modelPartBuilder.cuboid(cube.name, cube.offset.getX(), cube.offset.getY(), cube.offset.getZ(), cube.size.getX(), cube.size.getY(), cube.size.getZ(), dilation);

                modelPartBuilder.mirrored(cube.mirrored);
            }
            if (notEmpty(part.parent)) {
                ModelPartData parent = modelPartData.getChild(part.parent);
                parent.addChild(part.name, modelPartBuilder,
                        ModelTransform.of(
                                part.pivot.getX(), part.pivot.getY(), part.pivot.getZ(),
                                part.rotation.getX(), part.rotation.getY(), part.rotation.getZ()
                        )
                );
            } else {
                modelPartData.addChild(part.name, modelPartBuilder,
                        ModelTransform.of(
                                part.pivot.getX(), part.pivot.getY(), part.pivot.getZ(),
                                part.rotation.getX(), part.rotation.getY(), part.rotation.getZ()
                        )
                );
            }
        }
        return TexturedModelData.of(modelData, textureSize.x(), textureSize.y());
    }

    private static boolean notEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

}
