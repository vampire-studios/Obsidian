package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.utils.Vec2i;
import net.minecraft.client.model.*;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import java.util.List;

public class ArmorModel {

    public Identifier name;
    public Bone[] bones;
    public Vec2i textureSize = Vec2i.ZERO.copy();

    public static class Bone {

        public String name = "";
        public String parent = null;
        public Vector3f pivot = new Vector3f(0, 0, 0);
        public Vector3f rotation = new Vector3f(0, 0, 0);
        public List<Cube> cubes;
        public boolean mirrored = false;

        public static class Cube {
            public String name = "";
            public boolean mirrored = false;
            public Vec2i uv = Vec2i.ZERO.copy();
            public Vector3f offset = new Vector3f(0, 0, 0);
            public Vector3f size = new Vector3f(0, 0, 0);
            public Vector3f pivot = new Vector3f(0, 0, 0);
            public Vector3f radiusArray = new Vector3f(0, 0, 0);
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
                if(cube.radiusArray != null) dilation.add(cube.radiusArray.x(), cube.radiusArray.y(), cube.radiusArray.z());
                else if(cube.radius != 0) dilation.add(cube.radius);

                if (cube.name.isEmpty()) modelPartBuilder.cuboid(cube.offset.x(), cube.offset.y(), cube.offset.z(), cube.size.x(), cube.size.y(), cube.size.z(), dilation);
                else modelPartBuilder.cuboid(cube.name, cube.offset.x(), cube.offset.y(), cube.offset.z(), cube.size.x(), cube.size.y(), cube.size.z(), dilation);

                modelPartBuilder.mirrored(cube.mirrored);
            }
            if (notEmpty(part.parent)) {
                ModelPartData parent = modelPartData.getChild(part.parent);
                parent.addChild(part.name, modelPartBuilder,
                        ModelTransform.of(
                                part.pivot.x(), part.pivot.y(), part.pivot.z(),
                                part.rotation.x(), part.rotation.y(), part.rotation.z()
                        )
                );
            } else {
                modelPartData.addChild(part.name, modelPartBuilder,
                        ModelTransform.of(
                                part.pivot.x(), part.pivot.y(), part.pivot.z(),
                                part.rotation.x(), part.rotation.y(), part.rotation.z()
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
