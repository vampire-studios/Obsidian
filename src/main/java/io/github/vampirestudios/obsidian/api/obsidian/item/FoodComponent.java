package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
				if (Registry.STATUS_EFFECT.containsId(potionEffect.name)) {
					builder.statusEffect(new StatusEffectInstance(Registry.STATUS_EFFECT.get(potionEffect.name), potionEffect.duration * 20, potionEffect.amplifier, potionEffect.ambient, potionEffect.showParticles, potionEffect.showIcon), potionEffect.chance);
				}
			}
		}
		return builder;
	}

}
