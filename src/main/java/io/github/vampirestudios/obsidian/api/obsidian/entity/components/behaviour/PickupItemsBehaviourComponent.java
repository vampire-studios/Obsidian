package io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;

import java.util.List;

public class PickupItemsBehaviourComponent extends Component {

    public int priority;
    public List<String> damage_sources;
    public boolean force = false;
    public boolean ignore_mob_damage = false;
    public boolean prefer_water = false;
    public float speed_multiplier = 1.0F;

}