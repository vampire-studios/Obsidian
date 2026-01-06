package io.github.vampirestudios.obsidian.api.crucible.conditions;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConditionFactory {
    public static Condition createCondition(String type, Map<String, Object> parameters) {
        type = type.toLowerCase();

        if (matchesAlias(type, List.of("health", "hp", "healthCondition"))) {
            return parseHealthCondition(parameters);
        }
        else if (matchesAlias(type, List.of("crouching", "isCrouching", "crouchCondition"))) {
            boolean expectedOutcome = (boolean) parameters.getOrDefault("expectedOutcome", true);
            boolean applyToCaster = (boolean) parameters.getOrDefault("applyToCaster", true);
            return new CrouchingCondition(expectedOutcome, applyToCaster);
        }
        else if (matchesAlias(type, List.of("has_aura", "HasAura"))) {
            String auraId = (String) parameters.getOrDefault("aura", "");
            boolean expectedOutcome = (boolean) parameters.getOrDefault("expectedOutcome", true);
            return new HasAuraCondition(auraId, expectedOutcome);
        }
        else if (matchesAlias(type, List.of("variableEquals", "variableEqual", "VariableEquals", "VariableEqual", "variableequal", "variableequals", "variable_equals"))) {
            return parseVariableEqualsCondition(parameters);
        }
        else if (matchesAlias(type, List.of("variableIsSet", "variableisset", "variable_is_set"))) {
            return parseVariableIsSetCondition(parameters);
        }
        else if (matchesAlias(type, List.of("hasPotionEffect", "has_potion_effect", "haspotioneffect"))) {
            MobEffect effect = parsePotionEffect((String) parameters.get("effect"));
            int amplifier = (int) parameters.getOrDefault("amplifier", 0);
            return new HasPotionEffectCondition(effect, amplifier);
        }
        else if (matchesAlias(type, List.of("dayTime", "day", "daytime"))) {
            return new DayTimeCondition();
        }
        else if (matchesAlias(type, List.of("entityType", "entitytype", "entity_type"))) {
            EntityType<?> entityType = parseEntityType((String) parameters.get("type"));
            return new EntityTypeCondition(entityType);
        }
        else if (matchesAlias(type, List.of("onFire", "onfire", "on_fire"))) {
            return new OnFireCondition();
        }
        else if (matchesAlias(type, List.of("inWater", "inwater", "in_water"))) {
            return new InWaterCondition();
        }

        // Add cases for other conditions
        throw new IllegalArgumentException("Unknown condition type: " + type);
    }

    private static boolean matchesAlias(String type, List<String> aliases) {
        return aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(type));
    }


    public static Condition parseHealthCondition(Map<String, Object> parameters) {
        // Parse health attribute with comparison
        String healthParam = (String) parameters.getOrDefault("health", "0");
        HealthCondition.ComparisonOperator operator = HealthCondition.ComparisonOperator.EQUAL;
        double healthThreshold = 0;

        // Check for operators
        if (healthParam.startsWith("<=")) {
            operator = HealthCondition.ComparisonOperator.LESS_THAN_OR_EQUAL;
            healthThreshold = Double.parseDouble(healthParam.substring(2));
        } else if (healthParam.startsWith(">=")) {
            operator = HealthCondition.ComparisonOperator.GREATER_THAN_OR_EQUAL;
            healthThreshold = Double.parseDouble(healthParam.substring(2));
        } else if (healthParam.startsWith("<")) {
            operator = HealthCondition.ComparisonOperator.LESS_THAN;
            healthThreshold = Double.parseDouble(healthParam.substring(1));
        } else if (healthParam.startsWith(">")) {
            operator = HealthCondition.ComparisonOperator.GREATER_THAN;
            healthThreshold = Double.parseDouble(healthParam.substring(1));
        } else {
            // No operator, treat as EQUAL
            healthThreshold = Double.parseDouble(healthParam);
        }

        // Parse optional includeAbsorption attribute
        boolean includeAbsorption = Boolean.parseBoolean((String) parameters.getOrDefault("includeAbsorption", "false"));
        boolean applyToCaster = (boolean) parameters.getOrDefault("applyToCaster", true);

        return new HealthCondition(operator, healthThreshold, includeAbsorption, applyToCaster);
    }

    private static Condition parseVariableEqualsCondition(Map<String, Object> parameters) {
        String variable = (String) parameters.get("var");
        Object value = parameters.get("value");
        return new VariableEqualsCondition(variable, value);
    }

    private static Condition parseVariableIsSetCondition(Map<String, Object> parameters) {
        String variable = (String) parameters.get("var");
        return new VariableIsSetCondition(variable);
    }

    // Example parsing helpers:
    public static MobEffect parsePotionEffect(String effectName) {
        // Parse or find the MobEffect by name
        // Replace with actual lookup in your registry.
        return BuiltInRegistries.MOB_EFFECT.getValue(Identifier.parse(effectName.toLowerCase(Locale.ROOT)));
    }

    private static EntityType<?> parseEntityType(String typeName) {
        // Replace with actual lookup in your registry or entity type mapping.
        return BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(typeName.toLowerCase(Locale.ROOT)));
    }
}
