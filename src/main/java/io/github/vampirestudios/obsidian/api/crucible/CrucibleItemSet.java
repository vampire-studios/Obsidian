package io.github.vampirestudios.obsidian.api.crucible;

import java.util.Map;

public class CrucibleItemSet {
    public String Display;
    /** Map of minimum piece count → bonus for that tier. */
    public Map<Integer, ItemSetBonus> Bonuses;
}
