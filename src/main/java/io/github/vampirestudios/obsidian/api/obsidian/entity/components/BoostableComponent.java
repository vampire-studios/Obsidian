package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class BoostableComponent extends Component {

    public List<BoostItem> boost_items;
    public double duration = 3.0;
    public double speed_multiplier = 1.0;

    public static class BoostItem {

        public int damage;
        public ResourceLocation item;
        public ResourceLocation replace_items;

    }

}