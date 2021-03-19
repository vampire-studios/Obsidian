package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.util.registry.Registry;

public class FoodInformation {

	public Integer nutrition = 4;
	public String saturation_modifier = "";
	public boolean can_always_eat = false;
	public boolean snack = false;
	public boolean is_meat = false;
	public FoodPotionEffect[] effects;

	public float getSaturationModifier() {
		switch (saturation_modifier) {
			case "poor":
				return 0.1F;
			case "low":
				return 0.3F;
			case "normal":
				return 0.6F;
			case "good":
				return 0.8F;
			case "supernatural":
				return 1.2F;
			case "":
			default:
				return 0.0F;
		}
	}

	public FoodComponent.Builder getBuilder() {
		FoodComponent.Builder builder = new FoodComponent.Builder()
				.hunger(nutrition)
				.saturationModifier(getSaturationModifier());
		if (is_meat) builder.meat();
		if (can_always_eat) builder.alwaysEdible();
		if (snack) builder.snack();
		if (effects != null) {
			for (FoodPotionEffect potionEffect : effects) {
				builder.statusEffect(new StatusEffectInstance(StatusEffect.byRawId(Registry.STATUS_EFFECT.getRawId(Registry.STATUS_EFFECT.get(potionEffect.name))), potionEffect.duration * 20, potionEffect.amplifier), potionEffect.chance);
			}
		}
		return builder;
	}

}