package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.utils.Vec2i;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.List;

public class EntityModel {

    public ResourceLocation name;
    public ResourceLocation animation;
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

    public LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        for (EntityModel.Bone part : bones) {
            CubeListBuilder modelPartBuilder = CubeListBuilder.create();
            for (EntityModel.Bone.Cube cube : part.cubes) {
                modelPartBuilder.texOffs(cube.uv.x(), cube.uv.y());
                CubeDeformation dilation = CubeDeformation.NONE;
                if(cube.radiusArray != null) dilation.extend(cube.radiusArray.getX(), cube.radiusArray.getY(), cube.radiusArray.getZ());
                else dilation.extend(cube.radius);
                modelPartBuilder.addBox(cube.origin.x(), cube.origin.y(), cube.origin.z(),
                        cube.size.x(), cube.size.y(), cube.size.z(), dilation);
                modelPartBuilder.mirror(cube.mirrored);
            }
            if (part.parent != null && !part.parent.isEmpty() && !part.parent.isBlank()) modelPartData.getChild(part.parent)
                    .addOrReplaceChild(part.name, modelPartBuilder,
                            PartPose.offsetAndRotation(
                                    part.pivot.x(), part.pivot.y(), part.pivot.z(),
                                    part.rotation.x(), part.rotation.y(), part.rotation.z()
                            )
                    );
            else modelPartData.addOrReplaceChild(part.name, modelPartBuilder,
                    PartPose.offsetAndRotation(
                            part.pivot.x(), part.pivot.y(), part.pivot.z(),
                            part.rotation.x(), part.rotation.y(), part.rotation.z()
                    )
            );
        }
        return LayerDefinition.create(modelData, textureWidth, textureHeight);
    }

}
