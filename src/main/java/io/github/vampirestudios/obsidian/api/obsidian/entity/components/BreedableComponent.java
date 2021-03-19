package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.minecraft.util.Identifier;

import java.util.List;

public class BreedableComponent extends Component {

	public boolean allow_sitting = false;
	public boolean blend_attributes = true;
	public double breed_cooldown = 60;
	public List<Identifier> breed_items;
	public List<BreedableEntity> breeds_with;
	public boolean causes_pregnancy = false;

	public static class BreedableEntity {

		public Identifier baby_type;
		public Identifier breed_event;
		public Identifier mate_type;

	}

}