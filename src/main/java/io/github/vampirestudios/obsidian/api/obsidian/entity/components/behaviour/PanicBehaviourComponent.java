package io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class PanicBehaviourComponent extends Component {

    public int priority;
    public List<String> damage_sources;
    public ResourceLocation panic_sound;
    public SoundInterval sound_interval;
    public boolean force = false;
    public boolean ignore_mob_damage = false;
    public boolean prefer_water = false;
    public float speed_multiplier;

    public static class SoundInterval {
        public float range_min;
        public float range_max;
    }

}