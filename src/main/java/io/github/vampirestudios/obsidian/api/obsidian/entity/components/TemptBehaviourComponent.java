package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;

import java.util.List;

public class TemptBehaviourComponent extends Component {

    public int priority;
    public float speed_multiplier;
    public List<String> items;
    public boolean can_be_scared = false;

}