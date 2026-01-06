package io.github.vampirestudios.obsidian.api.obsidian.statusEffects;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.List;

public class StatusEffect {

    public NameInformation name;
    public String status_effect_type;
    public String color;
    public List<EffectAttributes> attributes;

    public MobEffectCategory getStatusEffectType() {
        return switch (status_effect_type) {
            case "beneficial" -> MobEffectCategory.BENEFICIAL;
            case "harmful" -> MobEffectCategory.HARMFUL;
            case "neutral" -> MobEffectCategory.NEUTRAL;
            default -> null;
        };
    }

    public int getColor() {
        String color1 = !this.color.isEmpty() && !this.color.isBlank() ? color.replace("#", "").replace("0x", "") : "ffffff";
        return Integer.parseInt(color1, 16);
    }

    public static class EffectAttributes {
        public Identifier attribute;
        public Identifier name;
        public double amount;
        public String operation;
    }

}
