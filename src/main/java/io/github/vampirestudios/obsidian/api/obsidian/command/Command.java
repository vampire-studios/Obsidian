package io.github.vampirestudios.obsidian.api.obsidian.command;

import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class Command {

    public String name;
    public String description;
    public int op_level;
    public Map<String, Node> arguments;
    public Identifier execute;

    public static class Node {
        Map<String, ArgumentNode> arguments;
        Map<String, LiteralNode> literals;
        List<String> execute;
        int opLevel;

        public Node(Map<String, ArgumentNode> arguments, Map<String, LiteralNode> literals, List<String> execute, int opLevel) {
            this.arguments = arguments;
            this.literals = literals;
            this.execute = execute;
            this.opLevel = opLevel;
        }

        public Map<String, ArgumentNode> getArguments() {
            return arguments;
        }

        public Map<String, LiteralNode> getLiterals() {
            return literals;
        }

        public List<String> getExecute() {
            return execute;
        }

        public int getOpLevel() {
            return opLevel;
        }
    }

    public static class ArgumentNode extends Node {
        private final ArgumentType argumentType;

        public ArgumentNode(ArgumentType type, Map<String, ArgumentNode> arguments, Map<String, LiteralNode> literals, List<String> execute, int opLevel) {
            super(arguments, literals, execute, opLevel);
            this.argumentType = type;
        }

        public ArgumentType getType() {
            return argumentType;
        }
    }

    public static class LiteralNode extends Node {
        public LiteralNode(Map<String, ArgumentNode> arguments, Map<String, LiteralNode> literals, List<String> execute, int opLevel) {
            super(arguments, literals, execute, opLevel);
        }
    }

    public static class CommandNode extends Node {
        private final String name;

        public CommandNode(String name, Map<String, ArgumentNode> arguments, Map<String, LiteralNode> literals, List<String> execute, int opLevel) {
            super(arguments, literals, execute, opLevel);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static enum ArgumentType {
        BLOCK_POS,
        PLAYER,
        ENTITY,
        INTEGER,
        LONG,
        BOOLEAN,
        DOUBLE,
        BIOME
    }
}
