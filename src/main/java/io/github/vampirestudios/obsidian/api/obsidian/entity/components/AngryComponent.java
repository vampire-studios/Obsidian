package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.minecraft.util.Identifier;

import java.util.List;

public class AngryComponent extends Component {

	public Identifier angry_sound;
	public boolean broadcast_anger = false;
	public boolean broadcast_anger_on_attack = false;
	public boolean broadcast_anger_on_being_attacked = false;
	public int broadcast_range = 20;
	public List<Identifier> broadcast_targets;
	public Identifier calm_event;
	public int duration = 25;
	public int duration_delta = 0;
	public int[] sound_interval = new int[]{
			0, 0
	};

}