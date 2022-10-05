package io.github.vampirestudios.obsidian.api.obsidian.block;

import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class CustomMaterial {

    public Identifier id;

    public String map_color;
    public boolean blocks_movement;
    public boolean burnable;
    public boolean liquid;
    public boolean blocks_light;
    public boolean replaceable;
    public boolean solid;
    public String piston_behavior;

    public MapColor getMapColor() {
        return switch(map_color) {
            case "CLEAR" -> MapColor.CLEAR;
            case "PALE_GREEN" -> MapColor.PALE_GREEN;
            case "PALE_YELLOW" -> MapColor.PALE_YELLOW;
            case "WHITE_GRAY" -> MapColor.WHITE_GRAY;
            case "BRIGHT_RED" -> MapColor.BRIGHT_RED;
            case "PALE_PURPLE" -> MapColor.PALE_PURPLE;
            case "IRON_GRAY" -> MapColor.IRON_GRAY;
            case "DARK_GREEN" -> MapColor.DARK_GREEN;
            case "WHITE" -> MapColor.WHITE;
            case "LIGHT_BLUE_GRAY" -> MapColor.LIGHT_BLUE_GRAY;
            case "DIRT_BROWN" -> MapColor.DIRT_BROWN;
            case "STONE_GRAY" -> MapColor.STONE_GRAY;
            case "WATER_BLUE" -> MapColor.WATER_BLUE;
            case "OAK_TAN" -> MapColor.OAK_TAN;
            case "OFF_WHITE" -> MapColor.OFF_WHITE;
            case "ORANGE" -> MapColor.ORANGE;
            case "MAGENTA" -> MapColor.MAGENTA;
            case "LIGHT_BLUE" -> MapColor.LIGHT_BLUE;
            case "YELLOW" -> MapColor.YELLOW;
            case "LIME" -> MapColor.LIME;
            case "PINK" -> MapColor.PINK;
            case "GRAY" -> MapColor.GRAY;
            case "LIGHT_GRAY" -> MapColor.LIGHT_GRAY;
            case "CYAN" -> MapColor.CYAN;
            case "PURPLE" -> MapColor.PURPLE;
            case "BLUE" -> MapColor.BLUE;
            case "BROWN" -> MapColor.BROWN;
            case "GREEN" -> MapColor.GREEN;
            case "RED" -> MapColor.RED;
            case "BLACK" -> MapColor.BLACK;
            case "GOLD" -> MapColor.GOLD;
            case "DIAMOND_BLUE" -> MapColor.DIAMOND_BLUE;
            case "LAPIS_BLUE" -> MapColor.LAPIS_BLUE;
            case "EMERALD_GREEN" -> MapColor.EMERALD_GREEN;
            case "SPRUCE_BROWN" -> MapColor.SPRUCE_BROWN;
            case "DARK_RED" -> MapColor.DARK_RED;
            case "TERRACOTTA_WHITE" -> MapColor.TERRACOTTA_WHITE;
            case "TERRACOTTA_ORANGE" -> MapColor.TERRACOTTA_ORANGE;
            case "TERRACOTTA_MAGENTA" -> MapColor.TERRACOTTA_MAGENTA;
            case "TERRACOTTA_LIGHT_BLUE" -> MapColor.TERRACOTTA_LIGHT_BLUE;
            case "TERRACOTTA_YELLOW" -> MapColor.TERRACOTTA_YELLOW;
            case "TERRACOTTA_LIME" -> MapColor.TERRACOTTA_LIME;
            case "TERRACOTTA_PINK" -> MapColor.TERRACOTTA_PINK;
            case "TERRACOTTA_GRAY" -> MapColor.TERRACOTTA_GRAY;
            case "TERRACOTTA_LIGHT_GRAY" -> MapColor.TERRACOTTA_LIGHT_GRAY;
            case "TERRACOTTA_CYAN" -> MapColor.TERRACOTTA_CYAN;
            case "TERRACOTTA_PURPLE" -> MapColor.TERRACOTTA_PURPLE;
            case "TERRACOTTA_BLUE" -> MapColor.TERRACOTTA_BLUE;
            case "TERRACOTTA_BROWN" -> MapColor.TERRACOTTA_BROWN;
            case "TERRACOTTA_GREEN" -> MapColor.TERRACOTTA_GREEN;
            case "TERRACOTTA_RED" -> MapColor.TERRACOTTA_RED;
            case "TERRACOTTA_BLACK" -> MapColor.TERRACOTTA_BLACK;
            case "DULL_RED" -> MapColor.DULL_RED;
            case "DULL_PINK" -> MapColor.DULL_PINK;
            case "DARK_CRIMSON" -> MapColor.DARK_CRIMSON;
            case "TEAL" -> MapColor.TEAL;
            case "DARK_AQUA" -> MapColor.DARK_AQUA;
            case "DARK_DULL_PINK" -> MapColor.DARK_DULL_PINK;
            case "BRIGHT_TEAL" -> MapColor.BRIGHT_TEAL;
            case "DEEPSLATE_GRAY" -> MapColor.DEEPSLATE_GRAY;
            case "RAW_IRON_PINK" -> MapColor.RAW_IRON_PINK;
            case "LICHEN_GREEN" -> MapColor.LICHEN_GREEN;
            default -> throw new IllegalStateException("Unexpected value: " + map_color);
        };
    }

    public PistonBehavior getPistonBehavior() {
        return switch(piston_behavior.toUpperCase(Locale.ROOT)) {
            case "NORMAL" -> PistonBehavior.NORMAL;
            case "DESTROY" -> PistonBehavior.DESTROY;
            case "BLOCK" -> PistonBehavior.BLOCK;
            case "IGNORE" -> PistonBehavior.IGNORE;
            case "PUSH_ONLY" -> PistonBehavior.PUSH_ONLY;
            default -> throw new IllegalStateException("Unexpected value: " + piston_behavior);
        };
    }

}
