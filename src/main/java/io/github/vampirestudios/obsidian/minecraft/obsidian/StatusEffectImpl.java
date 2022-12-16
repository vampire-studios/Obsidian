package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;

public class StatusEffectImpl extends StatusEffect {

    public io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect statusEffect;

    public StatusEffectImpl(io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect statusEffect) {
        super(statusEffect.getStatusEffectType(), statusEffect.getColor());
        for (io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect.EffectAttributes attribute : statusEffect.attributes) {
            this.addAttributeModifier(Registries.ATTRIBUTE.get(attribute.attribute), attribute.uuid, attribute.amount, EntityAttributeModifier.Operation.valueOf(attribute.operation));
        }
    }

    public StatusEffectImpl(StatusEffectCategory effectType, int color) {
        super(effectType, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
    }

}