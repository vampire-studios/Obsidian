package io.github.vampirestudios.obsidian.api.obsidian.command;

import com.mojang.brigadier.arguments.*;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.command.argument.*;
import net.minecraft.util.Identifier;

import java.util.HashMap;
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
        public boolean dedicatedOnly = false;
    }
    
    public class ArgumentNode extends Node {
        public String argument_type;
        public double min;
        public double max;

        public ArgumentType<?> getArgumentType(CommandBuildContext commandBuildContext) {
            Map<String, ArgumentType<?>> argumentTypeMap = new HashMap<>();
            argumentTypeMap.put("string", StringArgumentType.string());
            argumentTypeMap.put("word", StringArgumentType.word());
            argumentTypeMap.put("greedy", StringArgumentType.greedyString());
            argumentTypeMap.put("boolean", BoolArgumentType.bool());
            argumentTypeMap.put("double", DoubleArgumentType.doubleArg());
            argumentTypeMap.put("double_min", DoubleArgumentType.doubleArg(min));
            argumentTypeMap.put("double_min_max", DoubleArgumentType.doubleArg(min, max));
            argumentTypeMap.put("float", FloatArgumentType.floatArg());
            argumentTypeMap.put("float_min", FloatArgumentType.floatArg((float) min));
            argumentTypeMap.put("float_min_max", FloatArgumentType.floatArg((float) min, (float) max));
            argumentTypeMap.put("integer", IntegerArgumentType.integer());
            argumentTypeMap.put("integer_min", IntegerArgumentType.integer((int) min));
            argumentTypeMap.put("integer_min_max", IntegerArgumentType.integer((int) min, (int) max));

            argumentTypeMap.put("angel", AngleArgumentType.angle());

            //Block Stuff
            argumentTypeMap.put("block_pos", BlockPosArgumentType.blockPos());
            argumentTypeMap.put("block_predicate", BlockPredicateArgumentType.blockPredicate(commandBuildContext));
            argumentTypeMap.put("block_state", BlockStateArgumentType.blockState(commandBuildContext));

            argumentTypeMap.put("color", ColorArgumentType.color());

            argumentTypeMap.put("column_pos", ColumnPosArgumentType.columnPos());
            argumentTypeMap.put("command_function", CommandFunctionArgumentType.commandFunction());
            argumentTypeMap.put("dimension", DimensionArgumentType.dimension());
            argumentTypeMap.put("enchantment", EnchantmentArgumentType.enchantment());

            //Entity Stuff
            argumentTypeMap.put("entity_anchor", EntityAnchorArgumentType.entityAnchor());
            argumentTypeMap.put("entity", EntityArgumentType.entity());
            argumentTypeMap.put("player", EntityArgumentType.player());
            argumentTypeMap.put("players", EntityArgumentType.players());
            argumentTypeMap.put("entities", EntityArgumentType.entities());
            argumentTypeMap.put("entity_summon", EntitySummonArgumentType.entitySummon());
            argumentTypeMap.put("game_profile", GameProfileArgumentType.gameProfile());

            argumentTypeMap.put("identifier", IdentifierArgumentType.identifier());

            //Item Stuff
            argumentTypeMap.put("item_predicate", ItemPredicateArgumentType.itemPredicate(commandBuildContext));
            argumentTypeMap.put("item_slot", ItemSlotArgumentType.itemSlot());
            argumentTypeMap.put("item_stack", ItemStackArgumentType.itemStack(commandBuildContext));

            argumentTypeMap.put("message", MessageArgumentType.message());

            //NBT Stuff
            argumentTypeMap.put("nbt_compound", NbtCompoundArgumentType.nbtCompound());
            argumentTypeMap.put("nbt_element", NbtElementArgumentType.nbtElement());
            argumentTypeMap.put("nbt_path", NbtPathArgumentType.nbtPath());
            //Number Ranges
            argumentTypeMap.put("float_range", NumberRangeArgumentType.floatRange());
            argumentTypeMap.put("int_range", NumberRangeArgumentType.intRange());

            argumentTypeMap.put("operation", OperationArgumentType.operation());
            argumentTypeMap.put("particle_effect", ParticleEffectArgumentType.particleEffect());
            argumentTypeMap.put("rotation", RotationArgumentType.rotation());

            argumentTypeMap.put("scoreboard_criterion", ScoreboardCriterionArgumentType.scoreboardCriterion());
            argumentTypeMap.put("scoreboard_objective", ScoreboardObjectiveArgumentType.scoreboardObjective());
            argumentTypeMap.put("scoreboard_slot", ScoreboardSlotArgumentType.scoreboardSlot());

            argumentTypeMap.put("score_holder", ScoreHolderArgumentType.scoreHolder());
            argumentTypeMap.put("score_holders", ScoreHolderArgumentType.scoreHolders());

            argumentTypeMap.put("status_effect", StatusEffectArgumentType.statusEffect());

            argumentTypeMap.put("swizzle", SwizzleArgumentType.swizzle());
            argumentTypeMap.put("team", TeamArgumentType.team());
            argumentTypeMap.put("text", TextArgumentType.text());
            argumentTypeMap.put("time", TimeArgumentType.time());
            argumentTypeMap.put("uuid", UuidArgumentType.uuid());

            argumentTypeMap.put("vec2", Vec2ArgumentType.vec2());
            argumentTypeMap.put("vec3", Vec3ArgumentType.vec3());

            return argumentTypeMap.get(argument_type);
        }
    }

    public class LiteralNode extends Node {
    }

}
