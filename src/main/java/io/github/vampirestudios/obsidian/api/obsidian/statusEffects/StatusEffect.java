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
        return switch (status_effect_type) {
            case "beneficial" -> StatusEffectType.BENEFICIAL;
            case "harmful" -> StatusEffectType.HARMFUL;
            case "neutral" -> StatusEffectType.NEUTRAL;
            default -> null;
        };
    }

    public static class EffectAttributes {
        public Identifier attribute;
        public String uuid;
        public double amount;
        public String operation;
    }

}
