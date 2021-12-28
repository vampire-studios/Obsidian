package io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;

import java.util.List;

public class BegBehaviourComponent extends Component {

    public List<String> items;
    public float look_distance = 8.0F;
    public int[] look_time = new int[] {2, 4};

}