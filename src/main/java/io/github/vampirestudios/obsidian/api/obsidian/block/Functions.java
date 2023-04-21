package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

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
        public ResourceLocation function_file;
        public boolean requires_sneaking = false;
        public Predicate predicate;
    }

    public static class Function {
        public ResourceLocation function_file;
        public Predicate predicate = new Predicate();
        public FunctionType functionType = FunctionType.NONE;
        public ResourceLocation item;

        public enum FunctionType {
            NONE,
            REQUIRES_SHIFTING,
            REQUIRES_ITEM,
            REQUIRES_SHIFTING_AND_ITEM
        }

    }

    public static class Predicate {
        public PredicateType type = PredicateType.ALWAYS;
        public Tuple<String, String> values;

        public boolean matches() {
            return switch(type) {
                case NEVER -> false;
                case ALWAYS -> true;
                case EQUALS -> Objects.equals(values.getA(), values.getB());
                case NOT_EQUALS -> !Objects.equals(values.getA(), values.getB());
                case CONTAINS -> values.getA().contains(values.getB());
                case NOT_CONTAINS -> !values.getA().contains(values.getB());
                case BEGINS_WITH -> values.getA().startsWith(values.getB());
                case ENDS_WITH -> values.getA().endsWith(values.getB());
                case REGEX -> values.getA().matches(values.getB());
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
