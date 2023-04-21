package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class BribeableComponent extends Component {

    public double bribe_cooldown;
    public List<ResourceLocation> bribe_items;

}