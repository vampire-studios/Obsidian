package io.github.vampirestudios.obsidian.api.obsidian.statusEffects;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;

public class StatusEffect {

    public NameInformation name;
    public String status_effect_type;
    public String color;
    public Identifier icon;

    public StatusEffectType getStatusEffectType() {
        return switch (status_effect_type) {
            case "beneficial" -> StatusEffectType.BENEFICIAL;
            case "harmful" -> StatusEffectType.HARMFUL;
            case "neutral" -> StatusEffectType.NEUTRAL;
            default -> null;
        };
    }

}
