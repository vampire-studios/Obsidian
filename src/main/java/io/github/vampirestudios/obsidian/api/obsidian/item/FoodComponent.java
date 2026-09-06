package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class FoodComponent {

	/** How long a normal meal takes to eat, matching vanilla. */
	private static final float MEAL_SECONDS = 1.6F;

	/** How long a snack takes — dried kelp speed, half a meal. */
	private static final float SNACK_SECONDS = 0.8F;

	private static final int TICKS_PER_SECOND = 20;

	private static final Identifier DEFAULT_EAT_SOUND = Identifier.withDefaultNamespace("entity.generic.eat");
	private static final Identifier DEFAULT_DRINK_SOUND = Identifier.withDefaultNamespace("entity.generic.drink");

	public Identifier id;
	public int hunger = 4;
	public float saturation = 8.0F;
	public boolean can_always_eat = false;
	public boolean snack = false;
	public FoodPotionEffect[] effects = new FoodPotionEffect[0];

	public float getSaturationModifier() {
		return saturation;
	}

	public net.minecraft.world.food.FoodProperties.Builder getBuilder() {
		net.minecraft.world.food.FoodProperties.Builder builder = new net.minecraft.world.food.FoodProperties.Builder()
				.nutrition(hunger)
				.saturationModifier(getSaturationModifier());
		if (can_always_eat) builder.alwaysEdible();
		return builder;
	}

	/**
	 * How eating this food behaves: the animation, how long it takes, the sound, and any effects it
	 * applies.
	 *
	 * <p>Only nutrition stayed on food properties. Everything else about consuming — effects, speed,
	 * animation, sound — belongs to the consumable component, which is why a food that says nothing
	 * unusual still gets one built out of the defaults.
	 *
	 * @param info the item's {@code food_information}, or {@code null} for an edible block, which has
	 *             no eating overrides of its own
	 */
	public Consumable getConsumable(@Nullable FoodInformation info) {
		boolean drinkable = info != null && info.drinkable;

		Consumable.Builder builder = Consumable.builder()
				.consumeSeconds(consumeSeconds(info))
				.animation(drinkable ? ItemUseAnimation.DRINK : ItemUseAnimation.EAT)
				.sound(sound(info, drinkable));

		for (FoodPotionEffect effect : effects()) {
			MobEffectInstance instance = instanceOf(effect);
			// One consume effect per entry, so each keeps its own chance.
			if (instance != null) {
				builder.onConsume(new ApplyStatusEffectsConsumeEffect(List.of(instance), effect.chance));
			}
		}

		return builder.build();
	}

	private FoodPotionEffect[] effects() {
		return effects != null ? effects : new FoodPotionEffect[0];
	}

	/** An explicit {@code use_time} wins; otherwise a snack eats at half the speed of a meal. */
	private float consumeSeconds(@Nullable FoodInformation info) {
		if (info != null && info.useTime > 0) return (float) info.useTime / TICKS_PER_SECOND;
		return snack ? SNACK_SECONDS : MEAL_SECONDS;
	}

	/** The declared sound, falling back to the generic one if the pack named a sound that does not exist. */
	private static Holder<SoundEvent> sound(@Nullable FoodInformation info, boolean drinkable) {
		Identifier declared = info == null ? null : drinkable ? info.drinkSound : info.eatSound;
		Identifier fallback = drinkable ? DEFAULT_DRINK_SOUND : DEFAULT_EAT_SOUND;

		return resolveSound(declared).or(() -> resolveSound(fallback)).orElseThrow();
	}

	private static Optional<Holder.Reference<SoundEvent>> resolveSound(@Nullable Identifier id) {
		return id == null ? Optional.empty() : BuiltInRegistries.SOUND_EVENT.get(id);
	}

	/** Resolves one declared effect, or {@code null} if the pack named an effect that does not exist. */
	private static @Nullable MobEffectInstance instanceOf(@Nullable FoodPotionEffect effect) {
		if (effect == null || effect.effect == null) return null;

		return BuiltInRegistries.MOB_EFFECT.get(effect.effect)
				.map(holder -> new MobEffectInstance(holder, effect.duration * TICKS_PER_SECOND,
						effect.amplifier, effect.ambient, effect.showParticles, effect.showIcon))
				.orElse(null);
	}

}
