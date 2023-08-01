package io.github.vampirestudios.obsidian;

import java.awt.*;

public class BitShiftingThingy {
    public static void main(String[] args) {
        Color color = Color.WHITE;
        System.out.println((255 << 16) + (255 << 8) + 255);
    }
}
