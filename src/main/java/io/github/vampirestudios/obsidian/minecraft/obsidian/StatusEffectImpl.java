package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class StatusEffectImpl extends MobEffect {

    public io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect statusEffect;

    public StatusEffectImpl(io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect statusEffect) {
        super(statusEffect.getStatusEffectType(), statusEffect.getColor());
        for (io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect.EffectAttributes attribute : statusEffect.attributes) {
            this.addAttributeModifier(BuiltInRegistries.ATTRIBUTE.get(attribute.attribute), attribute.uuid, attribute.amount, AttributeModifier.Operation.valueOf(attribute.operation));
        }
    }

    public StatusEffectImpl(MobEffectCategory effectType, int color) {
        super(effectType, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
    }

}