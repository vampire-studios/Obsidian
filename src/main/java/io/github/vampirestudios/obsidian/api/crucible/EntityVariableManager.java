package io.github.vampirestudios.obsidian.api.crucible;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class EntityVariableManager {
    // Map to store variables for each entity
    private static final Map<LivingEntity, Map<String, Object>> entityVariables = new HashMap<>();

    // Method to set a variable for an entity
    public static void setEntityVariable(LivingEntity entity, String variableName, Object value) {
        entityVariables
                .computeIfAbsent(entity, k -> new HashMap<>())
                .put(variableName, value);
    }

    // Method to get a variable for an entity
    public static Object getEntityVariable(LivingEntity entity, String variableName) {
        Map<String, Object> variables = entityVariables.get(entity);
        return (variables != null) ? variables.get(variableName) : null;
    }

    // Method to check if a variable exists for an entity
    public static boolean hasEntityVariable(LivingEntity entity, String variableName) {
        Map<String, Object> variables = entityVariables.get(entity);
        return variables != null && variables.containsKey(variableName);
    }

    // Optional: Method to clear all variables for an entity
    public static void clearEntityVariables(LivingEntity entity) {
        entityVariables.remove(entity);
    }

    public static void unsetEntityVariable(LivingEntity entity, String variableName) {
        Map<String, Object> variables = entityVariables.get(entity);
        variables.remove(variableName);
    }

}
