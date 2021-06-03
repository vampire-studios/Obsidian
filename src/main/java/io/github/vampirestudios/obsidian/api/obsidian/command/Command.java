package io.github.vampirestudios.obsidian.api.obsidian.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.util.Identifier;

import java.util.Map;

public class Command {

    public String name;
    public String description;
    public int op_level;
    public Map<String, Node> arguments;
    public Identifier execute;

    public abstract class Node {
        public Map<String, ArgumentNode> arguments;
        public Map<String, LiteralNode> literals;
        public String[] executes;
        public Integer oplevel;
    }
    
    public class CommandNode extends Node {
        public String name;
    }
    
    public class ArgumentNode extends Node {
        public String argumentType;
        public ArgumentType<?> getArgumentType() {
            return switch (argumentType) {
                case "string" -> StringArgumentType.string();
                case "player" -> EntityArgumentType.player();
                case "blockpos" -> BlockPosArgumentType.blockPos();
                // Add your custom types here
                default -> null;
            };
        }
    }
    public class LiteralNode extends Node {
    }

}
