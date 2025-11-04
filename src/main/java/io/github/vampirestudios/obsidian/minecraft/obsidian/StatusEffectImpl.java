package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class StatusEffectImpl extends MobEffect {

    public io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect statusEffect;

    public StatusEffectImpl(io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect statusEffect) {
        super(statusEffect.getStatusEffectType(), statusEffect.getColor());
        for (io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect.EffectAttributes attribute : statusEffect.attributes) {
            this.addAttributeModifier(Holder.direct(BuiltInRegistries.ATTRIBUTE.getValue(attribute.attribute)), attribute.name, attribute.amount, AttributeModifier.Operation.valueOf(attribute.operation));
        }
    }

    public StatusEffectImpl(MobEffectCategory effectType, int color) {
        super(effectType, color);
    }

}