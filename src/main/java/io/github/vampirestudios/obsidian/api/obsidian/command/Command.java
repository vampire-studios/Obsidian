package io.github.vampirestudios.obsidian.api.obsidian.command;

import com.mojang.brigadier.arguments.*;
import net.minecraft.command.argument.*;
import net.minecraft.util.Identifier;

import java.util.Map;

public class Command {

    public abstract class Node {
        public Integer op_level;
        public Map<String, ArgumentNode> arguments;
        public Map<String, LiteralNode> literals;
        public String[] executes;
    }
    
    public class CommandNode extends Node {
        public Identifier name;
        public String description;
    }
    
    public class ArgumentNode extends Node {
        public String argumentType;
        public int min;
        public int max;
        public ArgumentType<?> getArgumentType() {
            switch (argumentType) {
                case "string": return StringArgumentType.string();
                case "word": return StringArgumentType.word();
                case "greedy": return StringArgumentType.greedyString();
                case "player": return EntityArgumentType.player();
                case "players": return EntityArgumentType.players();
                case "entity": return EntityArgumentType.entity();
                case "entities": return EntityArgumentType.entities();
                case "block_pos": return BlockPosArgumentType.blockPos();
                case "boolean": return BoolArgumentType.bool();
                case "double": return DoubleArgumentType.doubleArg();
                case "float": return FloatArgumentType.floatArg();
                case "integer": return IntegerArgumentType.integer();
                case "integer_min": return IntegerArgumentType.integer(min);
                case "integer_min_max": return IntegerArgumentType.integer(min, max);
                case "angle": return AngleArgumentType.angle();
                case "block_state": return BlockStateArgumentType.blockState();
                case "color": return ColorArgumentType.color();
                case "vec2": return Vec2ArgumentType.vec2();
                case "vec3": return Vec3ArgumentType.vec3();
                case "time": return TimeArgumentType.time();
                case "uuid": return UuidArgumentType.uuid();
                case "rotation": return RotationArgumentType.rotation();
                case "operation": return OperationArgumentType.operation();
                case "particle_effect": return ParticleEffectArgumentType.particleEffect();
                case "item_stack": return ItemStackArgumentType.itemStack();
                case "nbt_element": return NbtElementArgumentType.nbtElement();
                case "nbt_path": return NbtPathArgumentType.nbtPath();
                case "float_range": return NumberRangeArgumentType.method_30918();
                case "int_range": return NumberRangeArgumentType.intRange();
                case "identifier": return IdentifierArgumentType.identifier();
                // Add your custom types here
                default: return null;
            }
        }
    }

    public class LiteralNode extends Node {
    }

}
