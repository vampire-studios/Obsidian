package io.github.vampirestudios.obsidian.api.obsidian.command;

import com.mojang.brigadier.arguments.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.arguments.blocks.*;
import net.minecraft.commands.arguments.coordinates.*;
import net.minecraft.commands.arguments.item.*;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.gametest.framework.TestFunctionArgument;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
        public ResourceLocation name;
        public boolean dedicatedOnly = false;
    }
    
    public class ArgumentNode extends Node {
        public String argument_type;
        public double min;
        public double max;

        public ResourceLocation registry;

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

            argumentTypeMap.put("angel", AngleArgument.angle());

            //Block Stuff
            argumentTypeMap.put("block_mirror", TemplateMirrorArgument.templateMirror());
            argumentTypeMap.put("block_pos", BlockPosArgument.blockPos());
            argumentTypeMap.put("block_predicate", BlockPredicateArgument.blockPredicate(commandBuildContext));
            argumentTypeMap.put("block_rotation", TemplateRotationArgument.templateRotation());
            argumentTypeMap.put("block_state", BlockStateArgument.block(commandBuildContext));

            argumentTypeMap.put("color", ColorArgument.color());

            argumentTypeMap.put("column_pos", ColumnPosArgument.columnPos());

            argumentTypeMap.put("command_function", FunctionArgument.functions());
            argumentTypeMap.put("dimension", DimensionArgument.dimension());

            //Entity Stuff
            argumentTypeMap.put("entity_anchor", EntityAnchorArgument.anchor());
            argumentTypeMap.put("entity", EntityArgument.entity());
            argumentTypeMap.put("player", EntityArgument.player());
            argumentTypeMap.put("players", EntityArgument.players());
            argumentTypeMap.put("entities", EntityArgument.entities());

            argumentTypeMap.put("game_mode", GameModeArgument.gameMode());
            argumentTypeMap.put("game_profile", GameProfileArgument.gameProfile());

            argumentTypeMap.put("identifier", ResourceLocationArgument.id());

            //Item Stuff
            argumentTypeMap.put("item_predicate", ItemPredicateArgument.itemPredicate(commandBuildContext));
            argumentTypeMap.put("item_slot", SlotArgument.slot());
            argumentTypeMap.put("item_stack", ItemArgument.item(commandBuildContext));

            argumentTypeMap.put("message", MessageArgument.message());

            //NBT Stuff
            argumentTypeMap.put("nbt_compound", CompoundTagArgument.compoundTag());
            argumentTypeMap.put("nbt_element", NbtTagArgument.nbtTag());
            argumentTypeMap.put("nbt_path", NbtPathArgument.nbtPath());

            //Number Ranges
            argumentTypeMap.put("float_range", RangeArgument.floatRange());
            argumentTypeMap.put("int_range", RangeArgument.intRange());

            argumentTypeMap.put("operation", OperationArgument.operation());
            argumentTypeMap.put("particle_effect", ParticleArgument.particle(commandBuildContext));

            argumentTypeMap.put("registry_entry", ResourceArgument.resource(commandBuildContext, ResourceKey.createRegistryKey(registry)));
            argumentTypeMap.put("registry_entry_predicate", ResourceOrTagArgument.resourceOrTag(commandBuildContext, ResourceKey.createRegistryKey(registry)));
            argumentTypeMap.put("registry_key", ResourceKeyArgument.key(ResourceKey.createRegistryKey(registry)));
            argumentTypeMap.put("registry_predicate", ResourceOrTagKeyArgument.resourceOrTagKey(ResourceKey.createRegistryKey(registry)));

            argumentTypeMap.put("rotation", RotationArgument.rotation());

            argumentTypeMap.put("scoreboard_criterion", ObjectiveCriteriaArgument.criteria());
            argumentTypeMap.put("scoreboard_objective", ObjectiveArgument.objective());
            argumentTypeMap.put("scoreboard_slot", ScoreboardSlotArgument.displaySlot());

            argumentTypeMap.put("score_holder", ScoreHolderArgument.scoreHolder());
            argumentTypeMap.put("score_holders", ScoreHolderArgument.scoreHolders());

            argumentTypeMap.put("swizzle", SwizzleArgument.swizzle());
            argumentTypeMap.put("team", TeamArgument.team());
            argumentTypeMap.put("test_class", TestClassNameArgument.testClassName());
            argumentTypeMap.put("test_function", TestFunctionArgument.testFunctionArgument());
            argumentTypeMap.put("text", ComponentArgument.textComponent());
            argumentTypeMap.put("time", TimeArgument.time());
            argumentTypeMap.put("uuid", UuidArgument.uuid());

            argumentTypeMap.put("vec2", Vec2Argument.vec2());
            argumentTypeMap.put("vec3", Vec3Argument.vec3());

            return argumentTypeMap.get(argument_type);
        }
    }

    public class LiteralNode extends Node {
    }

}
