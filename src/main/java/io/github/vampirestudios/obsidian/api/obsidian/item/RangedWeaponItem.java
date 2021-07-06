package io.github.vampirestudios.obsidian.api.obsidian.item;

import java.util.List;

public class RangedWeaponItem extends Item {

    public List<String> overrides;
    public String weapon_type;

    public List<String> getOverrides() {
        if (weapon_type.equals("bow")) overrides.addAll(List.of("pull", "pulling"));
        if (weapon_type.equals("crossbow")) overrides.addAll(List.of("pull", "pulling", "charged", "firework"));
        if (weapon_type.equals("trident")) overrides.addAll(List.of("throwing"));
        return overrides;
    }

}