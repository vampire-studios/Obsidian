package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.minecraft.util.Identifier;

import java.util.List;

public class BreathableComponent extends Component {

	public List<Identifier> breathe_blocks;
	public boolean breathes_air = true;
	public boolean breathes_lava = true;
	public boolean breathes_solids = false;
	public boolean breathes_water = false;
	public boolean generates_bubbles = true;
	public int inhale_time = 0;
	public List<Identifier> non_breathe_blocks;
	public int suffocate_time = -20;
	public int total_supply = 15;

}