package io.github.vampirestudios.obsidian.minecraft.obsidian;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;

public final class Colors {
    public static final int GENERIC_DEFAULT_COLOR = 0xFFFFFF;
    public static final int STONE_DEFAULT_COLOR = 0x8F8F8F;

    public static final Object2IntMap<DyeColor> DYE_COLOR_RGB_VALUES = Util.make(new Object2IntOpenHashMap<>(), map -> {
        for (DyeColor color : DyeColor.values()) {
            map.put(color, toIntRgb(color.getColorComponents()));
        }
    });

    public static int toIntRgb(float[] rgb) {
        int r = (int) (rgb[0] * 255f);
        int g = (int) (rgb[1] * 255f);
        int b = (int) (rgb[2] * 255f);
        return (r << 16) | (g << 8) | b;
    }
}