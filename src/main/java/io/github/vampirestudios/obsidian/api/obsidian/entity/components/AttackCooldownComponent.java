package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;

public class AttackCooldownComponent extends Component {

	public String attack_cooldown_complete_event;
	public double[] attack_cooldown_time = new double[] {
			0.1, 1.0
	};

}