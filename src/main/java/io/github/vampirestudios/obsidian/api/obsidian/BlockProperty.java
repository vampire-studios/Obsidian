package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.util.Identifier;

import java.util.Map;

public class BlockProperty {

    public Map<String, Blockstate> variants;
//    public Map<String, Blockstate> properties;

    public static class Blockstate {

        /*public String type;
        public String name;
        @SerializedName("default")
        public String defaultValue;
        public int min;
        public int max;*/
        public Identifier model;
        public int x;
        public int y;
        public int z;

        /*public Property<?> getProperty() {
            return switch (type) {
                case "boolean" -> BooleanProperty.of(name);
                case "direction" -> DirectionProperty.of(name);
                case "int" -> IntProperty.of(name, this.min, this.max);
                case "block_half" -> EnumProperty.of(name, BlockHalf.class);
                case "double_block_half" -> EnumProperty.of(name, DoubleBlockHalf.class);
                case "wall_mount" -> EnumProperty.of(name, WallMountLocation.class);
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };
        }

        public <T extends Comparable<?>> T getProperty() {
            return switch (type) {
                case "boolean" -> Boolean.valueOf(defaultValue);
                case "direction" -> Direction.valueOf(defaultValue);
                case "int" -> Integer.valueOf(defaultValue);
                case "block_half" -> BlockHalf.valueOf(defaultValue);
                case "double_block_half" -> DoubleBlockHalf.valueOf(defaultValue);
                case "wall_mount" -> WallMountLocation.valueOf(defaultValue);
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };
        }*/

    }

}