package io.github.vampirestudios.obsidian.api.obsidian.command;

import com.mojang.brigadier.arguments.*;
import net.minecraft.command.argument.*;

import java.util.Map;

public class Command {

    public class Node {
        public String command_name;
        public String description;
        public Integer oplevel;
        public Map<String, ArgumentNode> arguments;
        public Map<String, LiteralNode> literals;
        public String[] executes;
    }
    
    public class CommandNode extends Node {
        public String name;
    }
    
    public class ArgumentNode extends Node {
        public String argumentType;
        public int min;
        public int max;
        public ArgumentType<?> getArgumentType() {
            return switch (argumentType) {
                case "string" -> StringArgumentType.string();
                case "word" -> StringArgumentType.word();
                case "greedy" -> StringArgumentType.greedyString();
                case "player" -> EntityArgumentType.player();
                case "block_pos" -> BlockPosArgumentType.blockPos();
                case "boolean" -> BoolArgumentType.bool();
                case "double" -> DoubleArgumentType.doubleArg();
                case "float" -> FloatArgumentType.floatArg();
                case "integer" -> IntegerArgumentType.integer();
                case "integer_min" -> IntegerArgumentType.integer(min);
                case "integer_min_max" -> IntegerArgumentType.integer(min, max);
                case "angle" -> AngleArgumentType.angle();
                case "block_state" -> BlockStateArgumentType.blockState();
                case "color" -> ColorArgumentType.color();
                case "vec2" -> Vec2ArgumentType.vec2();
                case "vec3" -> Vec3ArgumentType.vec3();
                case "time" -> TimeArgumentType.time();
                case "uuid" -> UuidArgumentType.uuid();
                case "rotation" -> RotationArgumentType.rotation();
                case "operation" -> OperationArgumentType.operation();
                case "particle_effect" -> ParticleEffectArgumentType.particleEffect();
                case "item_stack" -> ItemStackArgumentType.itemStack();
                case "nbt_element" -> NbtElementArgumentType.nbtElement();
                case "nbt_path" -> NbtPathArgumentType.nbtPath();
                case "float_range" -> NumberRangeArgumentType.floatRange();
                case "int_range" -> NumberRangeArgumentType.intRange();
                case "identifier" -> IdentifierArgumentType.identifier();
                // Add your custom types here
                default -> null;
            };
        }
    }

    public class LiteralNode extends Node {
    }

}
