package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Objects;

public class Functions {
    public Function random_tick;
    public Function scheduled_tick;
    public Function use;
    public Function step_on;
    public Function enter_collision;
    public Function place;
    @SerializedName("break")
    public Function break_block;
    public Function random_display_tick;

    public static class OptionalShiftFunction {
        public Identifier function_file;
        public boolean requires_sneaking = false;
        public Predicate predicate;
    }

    public static class Function {
        public Identifier function_file;
        public Predicate predicate = new Predicate();
        public FunctionType functionType = FunctionType.NONE;
        public Identifier item;

        public enum FunctionType {
            NONE,
            REQUIRES_SHIFTING,
            REQUIRES_ITEM,
            REQUIRES_SHIFTING_AND_ITEM
        }

    }

    public static class Predicate {
        public PredicateType type = PredicateType.ALWAYS;
        public Pair<String, String> values;

        public boolean matches() {
            return switch(type) {
                case NEVER -> false;
                case ALWAYS -> true;
                case EQUALS -> Objects.equals(values.getLeft(), values.getRight());
                case NOT_EQUALS -> !Objects.equals(values.getLeft(), values.getRight());
                case CONTAINS -> values.getLeft().contains(values.getRight());
                case NOT_CONTAINS -> !values.getLeft().contains(values.getRight());
                case BEGINS_WITH -> values.getLeft().startsWith(values.getRight());
                case ENDS_WITH -> values.getLeft().endsWith(values.getRight());
                case REGEX -> values.getLeft().matches(values.getRight());
            };
        }

        public enum PredicateType {
            NEVER,
            ALWAYS,
            EQUALS,
            NOT_EQUALS,
            CONTAINS,
            NOT_CONTAINS,
            BEGINS_WITH,
            ENDS_WITH,
            REGEX
        }
    }

}
