package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.minecraft.resources.ResourceLocation;

public class BarterComponent extends Component {

    public ResourceLocation barter_table;
    public int cooldown_after_being_attacked = 0;

}