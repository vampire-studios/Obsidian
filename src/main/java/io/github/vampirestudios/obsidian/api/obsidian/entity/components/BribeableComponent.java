package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.minecraft.util.Identifier;

import java.util.List;

public class BribeableComponent extends Component {

    public double bribe_cooldown;
    public List<Identifier> bribe_items;

}