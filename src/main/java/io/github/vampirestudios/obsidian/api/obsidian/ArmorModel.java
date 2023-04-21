package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.utils.Vec2i;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.List;

public class ArmorModel {

    public ResourceLocation name;
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

    public LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        for (ArmorModel.Bone part : bones) {
            CubeListBuilder modelPartBuilder = CubeListBuilder.create();
            for (ArmorModel.Bone.Cube cube : part.cubes) {
                modelPartBuilder.texOffs(cube.uv.x(), cube.uv.y());

                CubeDeformation dilation = CubeDeformation.NONE;
                if(cube.radiusArray != null) dilation.extend(cube.radiusArray.x(), cube.radiusArray.y(), cube.radiusArray.z());
                else if(cube.radius != 0) dilation.extend(cube.radius);

                if (cube.name.isEmpty()) modelPartBuilder.addBox(cube.offset.x(), cube.offset.y(), cube.offset.z(), cube.size.x(), cube.size.y(), cube.size.z(), dilation);
                else modelPartBuilder.addBox(cube.name, cube.offset.x(), cube.offset.y(), cube.offset.z(), cube.size.x(), cube.size.y(), cube.size.z(), dilation);

                modelPartBuilder.mirror(cube.mirrored);
            }
            if (notEmpty(part.parent)) {
                PartDefinition parent = modelPartData.getChild(part.parent);
                parent.addOrReplaceChild(part.name, modelPartBuilder,
                        PartPose.offsetAndRotation(
                                part.pivot.x(), part.pivot.y(), part.pivot.z(),
                                part.rotation.x(), part.rotation.y(), part.rotation.z()
                        )
                );
            } else {
                modelPartData.addOrReplaceChild(part.name, modelPartBuilder,
                        PartPose.offsetAndRotation(
                                part.pivot.x(), part.pivot.y(), part.pivot.z(),
                                part.rotation.x(), part.rotation.y(), part.rotation.z()
                        )
                );
            }
        }
        return LayerDefinition.create(modelData, textureSize.x(), textureSize.y());
    }

    private static boolean notEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

}
