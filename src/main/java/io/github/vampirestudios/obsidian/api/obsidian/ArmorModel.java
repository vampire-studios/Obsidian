package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.util.Identifier;

import java.util.List;

public class ArmorModel {

    public Identifier name;
    public Bone[] bones;
    public int textureWidth;
    public int textureHeight;

    public static class Bone {

        public String name = "";
        public String parent = null;
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
            public float[] radiusArray = new float[3];
            public float radius;
        }

    }

    private static boolean notEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    static class Test {

        public String name;
        public int u, v;

        public Test(String name, int u, int v) {
            this.name = name;
            this.u = u;
            this.v = v;
        }
    }

}
