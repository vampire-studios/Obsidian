package io.github.vampirestudios.obsidian.api.obsidian.block;

public class CustomBlockState {

    public Property[] properties;

    public static class Property {
        public boolean custom = true;
        public String type;
        public String[] values = new String[]{};
        public int number;
        public int[] range = new int[]{};
    }

}
