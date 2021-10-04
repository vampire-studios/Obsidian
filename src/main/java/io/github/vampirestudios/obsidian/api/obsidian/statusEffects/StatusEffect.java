package io.github.vampirestudios.obsidian.api.obsidian.statusEffects;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

import java.util.List;

public class StatusEffect {

    public NameInformation name;
    public String status_effect_type;
    public String color;
    public List<EffectAttributes> attributes;

    public StatusEffectCategory getStatusEffectType() {
        return switch (status_effect_type) {
            case "beneficial" -> StatusEffectCategory.BENEFICIAL;
            case "harmful" -> StatusEffectCategory.HARMFUL;
            case "neutral" -> StatusEffectCategory.NEUTRAL;
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
