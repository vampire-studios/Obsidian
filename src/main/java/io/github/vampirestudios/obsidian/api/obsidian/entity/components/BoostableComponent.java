package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.minecraft.util.Identifier;

import java.util.List;

public class BoostableComponent extends Component {

    public List<BoostItem> boost_items;
    public double duration = 3.0;
    public double speed_multiplier = 1.0;

    public static class BoostItem {

        public int damage;
        public Identifier item;
        public Identifier replace_items;

    }

}