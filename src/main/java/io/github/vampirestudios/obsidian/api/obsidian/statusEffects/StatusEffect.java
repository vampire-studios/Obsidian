package io.github.vampirestudios.obsidian.api.obsidian.statusEffects;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.entity.effect.StatusEffectType;

public class StatusEffect {

    public NameInformation name;
    public String status_effect_type;
    public String color;

    public StatusEffectType getStatusEffectType() {
        switch(status_effect_type) {
            case "beneficial":
                return StatusEffectType.BENEFICIAL;
            case "harmful":
                return StatusEffectType.HARMFUL;
            case "neutral":
                return StatusEffectType.NEUTRAL;
        }
        return null;
    }

}
