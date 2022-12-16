package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class FoodComponent {

	public Identifier id;
	public int hunger = 4;
	public float saturation = 8.0F;
	public boolean can_always_eat = false;
	public boolean snack = false;
	public boolean is_meat = false;
	public FoodPotionEffect[] effects = new FoodPotionEffect[0];

	public float getSaturationModifier() {
		return saturation;
	}

	public net.minecraft.item.FoodComponent.Builder getBuilder() {
		net.minecraft.item.FoodComponent.Builder builder = new net.minecraft.item.FoodComponent.Builder()
				.hunger(hunger)
				.saturationModifier(getSaturationModifier());
		if (is_meat) builder.meat();
		if (can_always_eat) builder.alwaysEdible();
		if (snack) builder.snack();
		if (effects != null) {
			for (FoodPotionEffect potionEffect : effects) {
				if (Registries.STATUS_EFFECT.containsId(potionEffect.effect)) {
					builder.statusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.get(potionEffect.effect), potionEffect.duration * 20, potionEffect.amplifier, potionEffect.ambient, potionEffect.showParticles, potionEffect.showIcon), potionEffect.chance);
				}
			}
		}
		return builder;
	}

}
