package io.github.vampirestudios.obsidian.api.obsidian.statusEffects;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;

import java.util.List;

public class StatusEffect {

    public NameInformation name;
    public String status_effect_type;
    public String color;
    public List<EffectAttributes> attributes;

    public StatusEffectType getStatusEffectType() {
        switch (status_effect_type) {
            case "beneficial": return StatusEffectType.BENEFICIAL;
            case "harmful": return StatusEffectType.HARMFUL;
            case "neutral": return StatusEffectType.NEUTRAL;
            default: return null;
        }
    }

    public static class EffectAttributes {
        public Identifier attribute;
        public String uuid;
        public double amount;
        public String operation;
    }

}
