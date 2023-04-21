package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class BreedableComponent extends Component {

    public boolean allow_sitting = false;
    public boolean blend_attributes = true;
    public double breed_cooldown = 60;
    public List<ResourceLocation> breed_items;
    public List<BreedableEntity> breeds_with;
    public boolean causes_pregnancy = false;

    public static class BreedableEntity {

        public ResourceLocation baby_type;
        public ResourceLocation breed_event;
        public ResourceLocation mate_type;

    }

}