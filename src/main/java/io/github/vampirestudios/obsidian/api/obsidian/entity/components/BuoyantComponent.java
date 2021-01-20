package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;

public class BuoyantComponent extends Component {

    public boolean apply_gravity = true;
    public double base_buoyancy = 1.0;
    public double big_wave_probability = 0.03;
    public double big_wave_speed = 10.0;
    public double drag_down_on_buoyancy_removed = 0.0;
    public boolean simulate_waves = true;

}