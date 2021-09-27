package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.common.collect.Lists;

import java.util.List;

public class RangedWeaponItem extends Item {

    public List<String> overrides;
    public String weapon_type;
    public boolean damageable = true;

    public List<String> getOverrides() {
        if (weapon_type.equals("bow")) overrides.addAll(Lists.newArrayList("pull", "pulling"));
        if (weapon_type.equals("crossbow")) overrides.addAll(Lists.newArrayList("pull", "pulling", "charged", "firework"));
        if (weapon_type.equals("trident")) overrides.addAll(Lists.newArrayList("throwing"));
        return overrides;
    }

}