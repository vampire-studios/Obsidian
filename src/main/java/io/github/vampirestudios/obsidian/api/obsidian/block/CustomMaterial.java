package io.github.vampirestudios.obsidian.api.obsidian.block;

import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;

public class CustomMaterial {

    public String map_color;
    public boolean allows_movement;
    public boolean burnable;
    public boolean liquid;
    public boolean allows_light;
    public boolean replaceable;
    public boolean solid;
    public String piston_behavior;

    public MapColor getMapColor() {
        switch(map_color) {
            case "CLEAR": return MapColor.CLEAR;
            case "PALE_GREEN": return MapColor.PALE_GREEN;
            case "PALE_YELLOW": return MapColor.PALE_YELLOW;
            case "WHITE_GRAY": return MapColor.WHITE_GRAY;
            case "BRIGHT_RED": return MapColor.BRIGHT_RED;
            case "PALE_PURPLE": return MapColor.PALE_PURPLE;
            case "IRON_GRAY": return MapColor.IRON_GRAY;
            case "DARK_GREEN": return MapColor.DARK_GREEN;
            case "WHITE": return MapColor.WHITE;
            case "LIGHT_BLUE_GRAY": return MapColor.LIGHT_BLUE_GRAY;
            case "DIRT_BROWN": return MapColor.DIRT_BROWN;
            case "STONE_GRAY": return MapColor.STONE_GRAY;
            case "WATER_BLUE": return MapColor.WATER_BLUE;
            case "OAK_TAN": return MapColor.OAK_TAN;
            case "OFF_WHITE": return MapColor.OFF_WHITE;
            case "ORANGE": return MapColor.ORANGE;
            case "MAGENTA": return MapColor.MAGENTA;
            case "LIGHT_BLUE": return MapColor.LIGHT_BLUE;
            case "YELLOW": return MapColor.YELLOW;
            case "LIME": return MapColor.LIME;
            case "PINK": return MapColor.PINK;
            case "GRAY": return MapColor.GRAY;
            case "LIGHT_GRAY": return MapColor.LIGHT_GRAY;
            case "CYAN": return MapColor.CYAN;
            case "PURPLE": return MapColor.PURPLE;
            case "BLUE": return MapColor.BLUE;
            case "BROWN": return MapColor.BROWN;
            case "GREEN": return MapColor.GREEN;
            case "RED": return MapColor.RED;
            case "BLACK": return MapColor.BLACK;
            case "GOLD": return MapColor.GOLD;
            case "DIAMOND_BLUE": return MapColor.DIAMOND_BLUE;
            case "LAPIS_BLUE": return MapColor.LAPIS_BLUE;
            case "EMERALD_GREEN": return MapColor.EMERALD_GREEN;
            case "SPRUCE_BROWN": return MapColor.SPRUCE_BROWN;
            case "DARK_RED": return MapColor.DARK_RED;
            case "TERRACOTTA_WHITE": return MapColor.TERRACOTTA_WHITE;
            case "TERRACOTTA_ORANGE": return MapColor.TERRACOTTA_ORANGE;
            case "TERRACOTTA_MAGENTA": return MapColor.TERRACOTTA_MAGENTA;
            case "TERRACOTTA_LIGHT_BLUE": return MapColor.TERRACOTTA_LIGHT_BLUE;
            case "TERRACOTTA_YELLOW": return MapColor.TERRACOTTA_YELLOW;
            case "TERRACOTTA_LIME": return MapColor.TERRACOTTA_LIME;
            case "TERRACOTTA_PINK": return MapColor.TERRACOTTA_PINK;
            case "TERRACOTTA_GRAY": return MapColor.TERRACOTTA_GRAY;
            case "TERRACOTTA_LIGHT_GRAY": return MapColor.TERRACOTTA_LIGHT_GRAY;
            case "TERRACOTTA_CYAN": return MapColor.TERRACOTTA_CYAN;
            case "TERRACOTTA_PURPLE": return MapColor.TERRACOTTA_PURPLE;
            case "TERRACOTTA_BLUE": return MapColor.TERRACOTTA_BLUE;
            case "TERRACOTTA_BROWN": return MapColor.TERRACOTTA_BROWN;
            case "TERRACOTTA_GREEN": return MapColor.TERRACOTTA_GREEN;
            case "TERRACOTTA_RED": return MapColor.TERRACOTTA_RED;
            case "TERRACOTTA_BLACK": return MapColor.TERRACOTTA_BLACK;
            default: throw new IllegalStateException("Unexpected value: " + map_color);
        }
    }

    public PistonBehavior getPistonBehavior() {
        switch(piston_behavior) {
            case "NORMAL": return PistonBehavior.NORMAL;
            case "DESTROY": return PistonBehavior.DESTROY;
            case "BLOCK": return PistonBehavior.BLOCK;
            case "IGNORE": return PistonBehavior.IGNORE;
            case "PUSH_ONLY": return PistonBehavior.PUSH_ONLY;
            default: throw new IllegalStateException("Unexpected value: " + piston_behavior);
        }
    }

}
