package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FoodInformation {

    public Integer hunger = 4;
    public float saturation = 8.0F;
    public String saturation_modifier = "";
    public boolean can_always_eat = false;
    public boolean snack = false;
    public boolean is_meat = false;
    public FoodPotionEffect[] effects = new FoodPotionEffect[0];
    public Identifier returnItem;
    public boolean drinkable;
    public Identifier drinkSound = new Identifier("entity.generic.drink");
    public Identifier eatSound = new Identifier("entity.generic.eat");
    public int use_time;

    public float getSaturationModifier() {
        if (!saturation_modifier.isEmpty()) {
            return switch (saturation_modifier) {
                case "poor" -> 0.1F;
                case "low" -> 0.3F;
                case "normal" -> 0.6F;
                case "good" -> 0.8F;
                case "supernatural" -> 1.2F;
                default -> 0.0F;
            };
        } else {
            return saturation;
        }
    }

    public FoodComponent.Builder getBuilder() {
        FoodComponent.Builder builder = new FoodComponent.Builder()
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