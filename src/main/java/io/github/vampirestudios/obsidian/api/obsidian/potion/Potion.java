package io.github.vampirestudios.obsidian.api.obsidian.potion;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;

public class Potion {

	/** The potion's registry id. Defaults to the file name. */
	public Identifier name;

	/** Every effect the potion applies. */
	public EffectInstance[] effects;

	/**
	 * The declared effects as vanilla instances. Entries naming an effect that does not exist are left
	 * out rather than failing the potion, so one bad id costs a line and not the whole bottle.
	 */
	public List<MobEffectInstance> getEffectInstances() {
		List<MobEffectInstance> instances = new ArrayList<>();
		if (effects == null) return instances;

		for (EffectInstance declared : effects) {
			MobEffectInstance instance = declared == null ? null : declared.toInstance();
			if (instance != null) instances.add(instance);
		}
		return instances;
	}

	/** Effects that named an effect id nothing has registered, for reporting. */
	public List<Identifier> getUnresolvedEffects() {
		List<Identifier> unresolved = new ArrayList<>();
		if (effects == null) return unresolved;

		for (EffectInstance declared : effects) {
			if (declared != null && declared.getEffect() == null && declared.effect != null) {
				unresolved.add(declared.effect);
			}
		}
		return unresolved;
	}

}
