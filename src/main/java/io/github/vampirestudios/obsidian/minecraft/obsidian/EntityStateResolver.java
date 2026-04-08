package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.google.gson.JsonElement;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.ComponentGroup;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Event;

import java.util.*;

public final class EntityStateResolver {
    private EntityStateResolver() {
    }

    public static Map<String, Object> createDefaultProperties(Entity entity) {
        Map<String, Object> properties = new HashMap<>();
        if (entity.properties == null) return properties;

        for (Map.Entry<String, Entity.EntityProperty> entry : entity.properties.entrySet()) {
            Entity.EntityProperty property = entry.getValue();
            Object value = property != null ? normalizePropertyValue(property, property.defaultValue) : null;
            properties.put(entry.getKey(), value);
        }

        return properties;
    }

    public static Object coercePropertyValue(Entity.EntityProperty property, JsonElement valueElement) {
        return normalizePropertyValue(property, jsonToObject(valueElement));
    }

    public static Object coercePropertyValue(Entity.EntityProperty property, Object value) {
        return normalizePropertyValue(property, value);
    }

    public static Map<String, Component> resolveActiveComponents(Entity entity, Map<String, Object> properties) {
        Map<String, Component> merged = new HashMap<>();
        if (entity.components != null) merged.putAll(entity.components);

        Map<String, ComponentGroup> componentSets = entity.getComponentSets();
        if (componentSets == null || componentSets.isEmpty()) return merged;

        for (Entity.StateRule state : entity.states) {
            if (!matchesStateRule(entity, state, properties)) continue;
            for (String setName : state.apply) {
                ComponentGroup group = componentSets.get(setName);
                if (group == null || group.components == null) continue;
                merged.putAll(group.components);
            }
        }

        return merged;
    }

    public static boolean evaluatePredicate(Entity entity, String predicateId, Map<String, Object> properties) {
        if (predicateId == null || predicateId.isBlank()) return true;
        if (entity.predicates == null) return false;
        Event.PredicateDefinition predicate = entity.predicates.get(predicateId);
        return evaluatePredicate(entity, predicate, properties, new HashSet<>());
    }

    public static boolean evaluatePredicate(Entity entity, Event.PredicateDefinition predicate, Map<String, Object> properties, Set<String> stack) {
        if (predicate == null) return true;

        if (predicate.ref != null && !predicate.ref.isBlank()) {
            if (!stack.add(predicate.ref)) return false;
            Event.PredicateDefinition referenced = entity.predicates != null ? entity.predicates.get(predicate.ref) : null;
            boolean result = evaluatePredicate(entity, referenced, properties, stack);
            stack.remove(predicate.ref);
            return result;
        }

        if (predicate.property != null && !predicate.property.isBlank()) {
            Object left = properties.get(predicate.property);

            if (predicate.equals != null && !Objects.equals(left, jsonToObject(predicate.equals))) {
                return false;
            }

            if (!predicate.in.isEmpty()) {
                boolean matched = false;
                for (JsonElement element : predicate.in) {
                    if (Objects.equals(left, jsonToObject(element))) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) return false;
            }

            if (predicate.value != null) {
                boolean leftBool = left instanceof Boolean b && b;
                if (leftBool != predicate.value) return false;
            }
        }

        if (!predicate.all.isEmpty()) {
            for (Event.PredicateDefinition child : predicate.all) {
                if (!evaluatePredicate(entity, child, properties, stack)) return false;
            }
        }

        if (!predicate.any.isEmpty()) {
            boolean any = false;
            for (Event.PredicateDefinition child : predicate.any) {
                if (evaluatePredicate(entity, child, properties, stack)) {
                    any = true;
                    break;
                }
            }
            if (!any) return false;
        }

        if (predicate.not != null && evaluatePredicate(entity, predicate.not, properties, stack)) return false;
        return true;
    }

    private static boolean matchesStateRule(Entity entity, Entity.StateRule state, Map<String, Object> properties) {
        if (state == null) return false;

        boolean predicateMatches = true;
        if (state.predicate != null && !state.predicate.isBlank()) {
            predicateMatches = evaluatePredicate(entity, state.predicate, properties);
        }
        if (!predicateMatches) return false;

        if (state.property == null || state.property.isBlank()) return predicateMatches;
        Object value = properties.get(state.property);

        if (state.equals != null) {
            Object expected = jsonToObject(state.equals);
            if (!Objects.equals(value, expected)) return false;
        }

        if (!state.in.isEmpty()) {
            for (JsonElement element : state.in) {
                if (Objects.equals(value, jsonToObject(element))) return true;
            }
            return false;
        }

        return true;
    }

    public static Object jsonToObject(JsonElement value) {
        if (value == null || value.isJsonNull()) return null;
        if (value.isJsonPrimitive()) {
            var primitive = value.getAsJsonPrimitive();
            if (primitive.isBoolean()) return primitive.getAsBoolean();
            if (primitive.isNumber()) {
                double number = primitive.getAsDouble();
                if (Math.floor(number) == number) return (int) number;
                return number;
            }
            return primitive.getAsString();
        }
        return value.toString();
    }

    private static Object normalizePropertyValue(Entity.EntityProperty property, Object value) {
        if (property == null || value == null) return value;
        String type = property.type != null ? property.type.toLowerCase(Locale.ROOT) : "string";

        return switch (type) {
            case "bool", "boolean" -> asBoolean(value);
            case "enum" -> normalizeEnum(property, value);
            case "int", "integer" -> asInt(value);
            case "float", "double", "number" -> asDouble(value);
            default -> String.valueOf(value);
        };
    }

    private static Object normalizeEnum(Entity.EntityProperty property, Object value) {
        String asString = String.valueOf(value);
        if (property.enumValues == null || property.enumValues.isEmpty()) return asString;
        return property.enumValues.contains(asString) ? asString : property.enumValues.get(0);
    }

    private static boolean asBoolean(Object value) {
        if (value instanceof Boolean b) return b;
        if (value instanceof Number n) return n.intValue() != 0;
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private static int asInt(Object value) {
        if (value instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static double asDouble(Object value) {
        if (value instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return 0.0D;
        }
    }
}
