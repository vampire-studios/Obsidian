package io.github.vampirestudios.obsidian.addonapi;

import java.util.Map;

public final class IconDefinition {
    public String item;                    // "minecraft:compass"
    public int count = 1;
    public Map<String, Object> components; // raw map, you can later bind to codecs
}