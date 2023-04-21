package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public class FoodComponent {

	public ResourceLocation id;
	public int hunger = 4;
	public float saturation = 8.0F;
	public boolean can_always_eat = false;
	public boolean snack = false;
	public boolean is_meat = false;
	public FoodPotionEffect[] effects = new FoodPotionEffect[0];

	public float getSaturationModifier() {
		return saturation;
	}

	public net.minecraft.world.food.FoodProperties.Builder getBuilder() {
		net.minecraft.world.food.FoodProperties.Builder builder = new net.minecraft.world.food.FoodProperties.Builder()
				.nutrition(hunger)
				.saturationMod(getSaturationModifier());
		if (is_meat) builder.meat();
		if (can_always_eat) builder.alwaysEat();
		if (snack) builder.fast();
		if (effects != null) {
			for (FoodPotionEffect potionEffect : effects) {
				if (BuiltInRegistries.MOB_EFFECT.containsKey(potionEffect.effect)) {
					builder.effect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.get(potionEffect.effect), potionEffect.duration * 20, potionEffect.amplifier, potionEffect.ambient, potionEffect.showParticles, potionEffect.showIcon), potionEffect.chance);
				}
			}
		}
		return builder;
	}

}
