package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.utils.Vec2i;
import net.minecraft.client.model.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;

import java.util.List;

public class EntityModel {

    public Identifier name;
    public Identifier animation;
    public Bone[] bones;
    public int textureWidth;
    public int textureHeight;

    public static class Bone {

        public String name = "";
        public String parent = "";
        public Vector3f pivot = new Vector3f(0, 0, 0);
        public Vector3f rotation = new Vector3f(0, 0, 0);
        public List<Cube> cubes;

        public static class Cube {
            public String name = "";
            public boolean mirrored = false;
            public Vec2i uv = Vec2i.ZERO.copy();
            public Vector3f origin = new Vector3f(0, 0, 0);
            public Vector3f size = new Vector3f(0, 0, 0);
            public Vec3i radiusArray = Vec3i.ZERO;
            public int radius;
        }

    }

    public TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        for (EntityModel.Bone part : bones) {
            ModelPartBuilder modelPartBuilder = ModelPartBuilder.create();
            for (EntityModel.Bone.Cube cube : part.cubes) {
                modelPartBuilder.uv(cube.uv.x(), cube.uv.y());
                Dilation dilation = Dilation.NONE;
                if(cube.radiusArray != null) dilation.add(cube.radiusArray.getX(), cube.radiusArray.getY(), cube.radiusArray.getZ());
                else dilation.add(cube.radius);
                modelPartBuilder.cuboid(cube.origin.x(), cube.origin.y(), cube.origin.z(),
                        cube.size.x(), cube.size.y(), cube.size.z(), dilation);
                modelPartBuilder.mirrored(cube.mirrored);
            }
            if (part.parent != null && !part.parent.isEmpty() && !part.parent.isBlank()) modelPartData.getChild(part.parent)
                    .addChild(part.name, modelPartBuilder,
                            ModelTransform.of(
                                    part.pivot.x(), part.pivot.y(), part.pivot.z(),
                                    part.rotation.x(), part.rotation.y(), part.rotation.z()
                            )
                    );
            else modelPartData.addChild(part.name, modelPartBuilder,
                    ModelTransform.of(
                            part.pivot.x(), part.pivot.y(), part.pivot.z(),
                            part.rotation.x(), part.rotation.y(), part.rotation.z()
                    )
            );
        }
        return TexturedModelData.of(modelData, textureWidth, textureHeight);
    }

}
