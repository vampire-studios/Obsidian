package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.client.model.*;
import net.minecraft.util.Identifier;

import java.util.List;

public class ArmorModel {

    public Identifier name;
    public Bone[] bones;
    public int textureWidth;
    public int textureHeight;

    public static class Bone {

        public String name = "";
        public String parent = "";
        public float[] pivot = new float[3];
        public float[] rotation = new float[3];
        public List<Cube> cubes;
        public boolean mirrored = false;

        public static class Cube {
            public String name = "";
            public boolean mirrored = false;
            public int[] uv = new int[2];
            public float[] offset = new float[3];
            public float[] size = new float[3];
            public float[] pivot = new float[3];
            public int[] radiusArray = new int[3];
            public int radius;
        }

    }

    public TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        for (ArmorModel.Bone part : bones) {
            ModelPartBuilder modelPartBuilder = ModelPartBuilder.create();
            for (ArmorModel.Bone.Cube cube : part.cubes) {
                modelPartBuilder.uv(cube.uv[0], cube.uv[1]);

                Dilation dilation = Dilation.NONE;
                if(cube.radiusArray != null) dilation.add(cube.radiusArray[0], cube.radiusArray[1], cube.radiusArray[2]);
                else dilation.add(cube.radius);

                if (cube.name.isEmpty()) modelPartBuilder.cuboid(cube.offset[0], cube.offset[1], cube.offset[2], cube.size[0], cube.size[1], cube.size[2], dilation);
                else modelPartBuilder.cuboid(cube.name, cube.offset[0], cube.offset[1], cube.offset[2], cube.size[0], cube.size[1], cube.size[2], dilation);

                modelPartBuilder.mirrored(cube.mirrored);
            }
            if (!part.parent.isEmpty()) modelPartData.getChild(part.parent)
                    .addChild(part.name, modelPartBuilder,
                            ModelTransform.of(
                                    part.pivot[0], part.pivot[1], part.pivot[2],
                                    part.rotation[0], part.rotation[1], part.rotation[2]
                            )
                    );
            else modelPartData.addChild(part.name, modelPartBuilder,
                    ModelTransform.of(
                            part.pivot[0], part.pivot[1], part.pivot[2],
                            part.rotation[0], part.rotation[1], part.rotation[2]
                    )
            );
        }
        return TexturedModelData.of(modelData, textureWidth, textureHeight);
    }

}
