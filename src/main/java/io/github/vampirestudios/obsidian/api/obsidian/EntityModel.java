package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.utils.Vec2i;
import net.minecraft.client.model.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public class EntityModel {

    public Identifier name;
    public Bone[] bones;
    public int textureWidth;
    public int textureHeight;

    public static class Bone {

        public String name = "";
        public String parent = "";
        public Vec3f pivot = Vec3f.ZERO.copy();
        public Vec3f rotation = Vec3f.ZERO.copy();
        public List<Cube> cubes;

        public static class Cube {
            public String name = "";
            public boolean mirrored = false;
            public Vec2i uv = Vec2i.ZERO.copy();
            public Vec3f origin = Vec3f.ZERO.copy();
            public Vec3f size = Vec3f.ZERO.copy();
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
                modelPartBuilder.cuboid(cube.origin.getX(), cube.origin.getY(), cube.origin.getZ(),
                        cube.size.getX(), cube.size.getY(), cube.size.getZ(), dilation);
                modelPartBuilder.mirrored(cube.mirrored);
            }
            if (part.parent != null && !part.parent.isEmpty() && !part.parent.isBlank()) modelPartData.getChild(part.parent)
                    .addChild(part.name, modelPartBuilder,
                            ModelTransform.of(
                                    part.pivot.getX(), part.pivot.getY(), part.pivot.getZ(),
                                    part.rotation.getX(), part.rotation.getY(), part.rotation.getZ()
                            )
                    );
            else modelPartData.addChild(part.name, modelPartBuilder,
                    ModelTransform.of(
                            part.pivot.getX(), part.pivot.getY(), part.pivot.getZ(),
                            part.rotation.getX(), part.rotation.getY(), part.rotation.getZ()
                    )
            );
        }
        return TexturedModelData.of(modelData, textureWidth, textureHeight);
    }

}
