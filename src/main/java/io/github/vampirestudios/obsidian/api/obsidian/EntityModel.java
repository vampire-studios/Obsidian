package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import java.util.List;
import java.util.Map;

public class EntityModel {

    public Identifier name;
    public ModelPart[] parts;
    public int textureWidth;
    public int textureHeight;

    public static class ModelPart {

        public String name;
        public float pivotX = 0.0F;
        public float pivotY = 0.0F;
        public float pivotZ = 0.0F;
        public float pitch = 0.0F;
        public float yaw = 0.0F;
        public float roll = 0.0F;
        public boolean visible = true;
        public List<ModelPart.Cuboid> cuboids;
        public Map<String, ModelPart> children;

        public static class Vertex {
            public Vec3f pos;
            public float u;
            public float v;
        }

        static class Quad {
            public ModelPart.Vertex[] vertices;
            public Vec3f direction;
        }

        public static class Cuboid {
            public ModelPart.Quad[] sides;
            public int textureX;
            public int textureY;
            public float offsetX;
            public float offsetY;
            public float offsetZ;
            public float sizeX;
            public float sizeY;
            public float sizeZ;
        }

    }

}
