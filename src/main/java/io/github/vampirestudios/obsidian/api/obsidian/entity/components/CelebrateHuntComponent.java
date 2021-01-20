package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.minecraft.util.Identifier;

public class CelebrateHuntComponent extends Component {

    public boolean broadcast = true;
    public Identifier celebrate_sound;
    public int duration = 4;
    public double radius = 12;
    public int[] sound_interval = new int[] {
            0
    };

}