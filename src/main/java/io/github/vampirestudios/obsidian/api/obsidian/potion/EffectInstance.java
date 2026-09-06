package io.github.vampirestudios.obsidian.api.obsidian.potion;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.jspecify.annotations.Nullable;

/**
 * One effect a potion applies, in the same shape food and {@code apply_effect} use — an effect id, a
 * duration in ticks, and a strength.
 */
public class EffectInstance {

	/** The status effect to apply, vanilla's or one from {@code status_effect}. */
	@SerializedName(value = "effect", alternate = {"name"})
	public Identifier effect;

	/** Length in ticks: 20 per second. */
	public int duration;

	/** Strength, zero-based. {@code 0} is level I. */
	public int amplifier;

	/** Beacon-style presentation: different colour, no countdown. */
	public boolean ambient = false;

	@SerializedName("show_particles")
	public boolean showParticles = true;

	@SerializedName("show_icon")
	public boolean showIcon = true;

	/** The effect this entry names, or null when it names none or names one that does not exist. */
	public @Nullable Holder<MobEffect> getEffect() {
		if (effect == null) return null;
		return BuiltInRegistries.MOB_EFFECT.get(effect).map(holder -> (Holder<MobEffect>) holder).orElse(null);
	}

	/** This entry as a vanilla effect instance, or null when its effect could not be resolved. */
	public @Nullable MobEffectInstance toInstance() {
		Holder<MobEffect> holder = getEffect();
		if (holder == null) return null;
		return new MobEffectInstance(holder, duration, amplifier, ambient, showParticles, showIcon);
	}

}
