package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.util.Identifier;

import java.util.Map;

public class BlockstateInformation {

    public Map<String, Blockstate> variants;

    public static class Blockstate {

        public Identifier model;
        public int x;
        public int y;
        public int z;

    }

}