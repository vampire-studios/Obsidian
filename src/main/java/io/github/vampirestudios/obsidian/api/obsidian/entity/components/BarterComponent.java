package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.minecraft.util.Identifier;

public class BarterComponent extends Component {

    public Identifier barter_table;
    public int cooldown_after_being_attacked = 0;

}