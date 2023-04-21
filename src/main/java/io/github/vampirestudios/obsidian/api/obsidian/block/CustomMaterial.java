package io.github.vampirestudios.obsidian.api.obsidian.block;

import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

public class CustomMaterial {

    public ResourceLocation id;

    public String map_color;
    public boolean blocks_movement;
    public boolean burnable;
    public boolean liquid;
    public boolean blocks_light;
    public boolean replaceable;
    public boolean solid;
    public String piston_behavior;

    public MaterialColor getMapColor() {
        return switch(map_color) {
            case "CLEAR" -> MaterialColor.NONE;
            case "PALE_GREEN" -> MaterialColor.GRASS;
            case "PALE_YELLOW" -> MaterialColor.SAND;
            case "WHITE_GRAY" -> MaterialColor.WOOL;
            case "BRIGHT_RED" -> MaterialColor.FIRE;
            case "PALE_PURPLE" -> MaterialColor.ICE;
            case "IRON_GRAY" -> MaterialColor.METAL;
            case "DARK_GREEN" -> MaterialColor.PLANT;
            case "WHITE" -> MaterialColor.SNOW;
            case "LIGHT_BLUE_GRAY" -> MaterialColor.CLAY;
            case "DIRT_BROWN" -> MaterialColor.DIRT;
            case "STONE_GRAY" -> MaterialColor.STONE;
            case "WATER_BLUE" -> MaterialColor.WATER;
            case "OAK_TAN" -> MaterialColor.WOOD;
            case "OFF_WHITE" -> MaterialColor.QUARTZ;
            case "ORANGE" -> MaterialColor.COLOR_ORANGE;
            case "MAGENTA" -> MaterialColor.COLOR_MAGENTA;
            case "LIGHT_BLUE" -> MaterialColor.COLOR_LIGHT_BLUE;
            case "YELLOW" -> MaterialColor.COLOR_YELLOW;
            case "LIME" -> MaterialColor.COLOR_LIGHT_GREEN;
            case "PINK" -> MaterialColor.COLOR_PINK;
            case "GRAY" -> MaterialColor.COLOR_GRAY;
            case "LIGHT_GRAY" -> MaterialColor.COLOR_LIGHT_GRAY;
            case "CYAN" -> MaterialColor.COLOR_CYAN;
            case "PURPLE" -> MaterialColor.COLOR_PURPLE;
            case "BLUE" -> MaterialColor.COLOR_BLUE;
            case "BROWN" -> MaterialColor.COLOR_BROWN;
            case "GREEN" -> MaterialColor.COLOR_GREEN;
            case "RED" -> MaterialColor.COLOR_RED;
            case "BLACK" -> MaterialColor.COLOR_BLACK;
            case "GOLD" -> MaterialColor.GOLD;
            case "DIAMOND_BLUE" -> MaterialColor.DIAMOND;
            case "LAPIS_BLUE" -> MaterialColor.LAPIS;
            case "EMERALD_GREEN" -> MaterialColor.EMERALD;
            case "SPRUCE_BROWN" -> MaterialColor.PODZOL;
            case "DARK_RED" -> MaterialColor.NETHER;
            case "TERRACOTTA_WHITE" -> MaterialColor.TERRACOTTA_WHITE;
            case "TERRACOTTA_ORANGE" -> MaterialColor.TERRACOTTA_ORANGE;
            case "TERRACOTTA_MAGENTA" -> MaterialColor.TERRACOTTA_MAGENTA;
            case "TERRACOTTA_LIGHT_BLUE" -> MaterialColor.TERRACOTTA_LIGHT_BLUE;
            case "TERRACOTTA_YELLOW" -> MaterialColor.TERRACOTTA_YELLOW;
            case "TERRACOTTA_LIME" -> MaterialColor.TERRACOTTA_LIGHT_GREEN;
            case "TERRACOTTA_PINK" -> MaterialColor.TERRACOTTA_PINK;
            case "TERRACOTTA_GRAY" -> MaterialColor.TERRACOTTA_GRAY;
            case "TERRACOTTA_LIGHT_GRAY" -> MaterialColor.TERRACOTTA_LIGHT_GRAY;
            case "TERRACOTTA_CYAN" -> MaterialColor.TERRACOTTA_CYAN;
            case "TERRACOTTA_PURPLE" -> MaterialColor.TERRACOTTA_PURPLE;
            case "TERRACOTTA_BLUE" -> MaterialColor.TERRACOTTA_BLUE;
            case "TERRACOTTA_BROWN" -> MaterialColor.TERRACOTTA_BROWN;
            case "TERRACOTTA_GREEN" -> MaterialColor.TERRACOTTA_GREEN;
            case "TERRACOTTA_RED" -> MaterialColor.TERRACOTTA_RED;
            case "TERRACOTTA_BLACK" -> MaterialColor.TERRACOTTA_BLACK;
            case "DULL_RED" -> MaterialColor.CRIMSON_NYLIUM;
            case "DULL_PINK" -> MaterialColor.CRIMSON_STEM;
            case "DARK_CRIMSON" -> MaterialColor.CRIMSON_HYPHAE;
            case "TEAL" -> MaterialColor.WARPED_NYLIUM;
            case "DARK_AQUA" -> MaterialColor.WARPED_STEM;
            case "DARK_DULL_PINK" -> MaterialColor.WARPED_HYPHAE;
            case "BRIGHT_TEAL" -> MaterialColor.WARPED_WART_BLOCK;
            case "DEEPSLATE_GRAY" -> MaterialColor.DEEPSLATE;
            case "RAW_IRON_PINK" -> MaterialColor.RAW_IRON;
            case "LICHEN_GREEN" -> MaterialColor.GLOW_LICHEN;
            default -> throw new IllegalStateException("Unexpected value: " + map_color);
        };
    }

    public PushReaction getPistonBehavior() {
        return switch(piston_behavior.toUpperCase(Locale.ROOT)) {
            case "NORMAL" -> PushReaction.NORMAL;
            case "DESTROY" -> PushReaction.DESTROY;
            case "BLOCK" -> PushReaction.BLOCK;
            case "IGNORE" -> PushReaction.IGNORE;
            case "PUSH_ONLY" -> PushReaction.PUSH_ONLY;
            default -> throw new IllegalStateException("Unexpected value: " + piston_behavior);
        };
    }

}
