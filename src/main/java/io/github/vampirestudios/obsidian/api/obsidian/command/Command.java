package io.github.vampirestudios.obsidian.api.obsidian.command;

import com.mojang.brigadier.arguments.*;
import io.github.vampirestudios.obsidian.minecraft.ModIdArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.*;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.gametest.framework.TestFunctionArgument;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class Command {
    public abstract class Node {
        public Map<String, ArgumentNode> arguments;
        public Map<String, LiteralNode> literals;
        public String[] executes;
        public String[] aliases;
        public Integer op_level;
    }
    
    public class CommandNode extends Node {
        public ResourceLocation name;
        public boolean dedicatedOnly = false;
    }
    
    public class ArgumentNode extends Node {
        public String type;
        public double min;
        public double max;
        public boolean center_integers;

        public ArgumentType<?> getArgumentType(CommandBuildContext commandBuildContext) {
            Map<String, ArgumentType<?>> argumentTypeMap = new HashMap<>();
            //Text stuff
            argumentTypeMap.put("brigadier:string", StringArgumentType.string());
            argumentTypeMap.put("brigadier:word", StringArgumentType.word());
            argumentTypeMap.put("brigadier:greedy", StringArgumentType.greedyString());
            argumentTypeMap.put("component", ComponentArgument.textComponent());
            argumentTypeMap.put("message", MessageArgument.message());

            argumentTypeMap.put("brigadier:bool", BoolArgumentType.bool());

            argumentTypeMap.put("angel", AngleArgument.angle());

            //Block Stuff
            argumentTypeMap.put("block_predicate", BlockPredicateArgument.blockPredicate(commandBuildContext));
            argumentTypeMap.put("block_state", BlockStateArgument.block(commandBuildContext));

            //Structure Stuff
            argumentTypeMap.put("template_mirror", TemplateMirrorArgument.templateMirror());
            argumentTypeMap.put("template_rotation", TemplateRotationArgument.templateRotation());

            //Entity Stuff
            argumentTypeMap.put("entity_anchor", EntityAnchorArgument.anchor());
            argumentTypeMap.put("entity", EntityArgument.entity());
            argumentTypeMap.put("entities", EntityArgument.entities());
            argumentTypeMap.put("uuid", UuidArgument.uuid());
            argumentTypeMap.put("team", TeamArgument.team());

            //Player Stuff
            argumentTypeMap.put("player", EntityArgument.player());
            argumentTypeMap.put("players", EntityArgument.players());
            argumentTypeMap.put("game_profile", GameProfileArgument.gameProfile());
            argumentTypeMap.put("game_mode", GameModeArgument.gameMode());

            //Item Stuff
            argumentTypeMap.put("item_predicate", ItemPredicateArgument.itemPredicate(commandBuildContext));
            argumentTypeMap.put("item_slot", SlotArgument.slot());
            argumentTypeMap.put("item", ItemArgument.item(commandBuildContext));

            //NBT Stuff
            argumentTypeMap.put("nbt_compound_tag", CompoundTagArgument.compoundTag());
            argumentTypeMap.put("nbt_tag", NbtTagArgument.nbtTag());
            argumentTypeMap.put("nbt_path", NbtPathArgument.nbtPath());

            //Number Ranges
            argumentTypeMap.put("float_range", RangeArgument.floatRange());
            argumentTypeMap.put("int_range", RangeArgument.intRange());

            //Math
            argumentTypeMap.put("brigadier:double", DoubleArgumentType.doubleArg());
            argumentTypeMap.put("brigadier:double_min", DoubleArgumentType.doubleArg(min));
            argumentTypeMap.put("brigadier:double_min_max", DoubleArgumentType.doubleArg(min, max));
            argumentTypeMap.put("brigadier:float", FloatArgumentType.floatArg());
            argumentTypeMap.put("brigadier:float_min", FloatArgumentType.floatArg((float) min));
            argumentTypeMap.put("brigadier:float_min_max", FloatArgumentType.floatArg((float) min, (float) max));
            argumentTypeMap.put("brigadier:integer", IntegerArgumentType.integer());
            argumentTypeMap.put("brigadier:integer_min", IntegerArgumentType.integer((int) min));
            argumentTypeMap.put("brigadier:integer_min_max", IntegerArgumentType.integer((int) min, (int) max));
            argumentTypeMap.put("brigadier:long", LongArgumentType.longArg());
            argumentTypeMap.put("brigadier:long_min", LongArgumentType.longArg((long) min));
            argumentTypeMap.put("brigadier:long_min_max", LongArgumentType.longArg((long) min, (long) max));
            argumentTypeMap.put("operation", OperationArgument.operation());

            //Registry Stuff
            argumentTypeMap.put("resource_block", ResourceArgument.resource(commandBuildContext, Registries.BLOCK));
            argumentTypeMap.put("resource_item", ResourceArgument.resource(commandBuildContext, Registries.ITEM));
            argumentTypeMap.put("resource_entity", ResourceArgument.resource(commandBuildContext, Registries.ENTITY_TYPE));
            argumentTypeMap.put("resource_effect", ResourceArgument.resource(commandBuildContext, Registries.MOB_EFFECT));
            argumentTypeMap.put("resource_enchantment", ResourceArgument.resource(commandBuildContext, Registries.ENCHANTMENT));
            argumentTypeMap.put("resource_biome", ResourceArgument.resource(commandBuildContext, Registries.BIOME));
            argumentTypeMap.put("resource_feature", ResourceArgument.resource(commandBuildContext, Registries.FEATURE));
            argumentTypeMap.put("resource_structure", ResourceArgument.resource(commandBuildContext, Registries.STRUCTURE));

            argumentTypeMap.put("resource_or_tag_block", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.BLOCK));
            argumentTypeMap.put("resource_or_tag_item", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.ITEM));
            argumentTypeMap.put("resource_or_tag_entity", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.ENTITY_TYPE));
            argumentTypeMap.put("resource_or_tag_effect", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.MOB_EFFECT));
            argumentTypeMap.put("resource_or_tag_enchantment", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.ENCHANTMENT));
            argumentTypeMap.put("resource_or_tag_biome", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.BIOME));
            argumentTypeMap.put("resource_or_tag_feature", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.FEATURE));
            argumentTypeMap.put("resource_or_tag_structure", ResourceOrTagArgument.resourceOrTag(commandBuildContext, Registries.STRUCTURE));

            argumentTypeMap.put("resource_key_block", ResourceKeyArgument.key(Registries.BLOCK));
            argumentTypeMap.put("resource_key_item", ResourceKeyArgument.key(Registries.ITEM));
            argumentTypeMap.put("resource_key_entity", ResourceKeyArgument.key(Registries.ENTITY_TYPE));
            argumentTypeMap.put("resource_key_effect", ResourceKeyArgument.key(Registries.MOB_EFFECT));
            argumentTypeMap.put("resource_key_enchantment", ResourceKeyArgument.key(Registries.ENCHANTMENT));
            argumentTypeMap.put("resource_key_biome", ResourceKeyArgument.key(Registries.BIOME));
            argumentTypeMap.put("resource_key_feature", ResourceKeyArgument.key(Registries.FEATURE));
            argumentTypeMap.put("resource_key_structure", ResourceKeyArgument.key(Registries.STRUCTURE));

            argumentTypeMap.put("resource_or_tag_key_block", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.BLOCK));
            argumentTypeMap.put("resource_or_tag_key_item", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM));
            argumentTypeMap.put("resource_or_tag_key_entity", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ENTITY_TYPE));
            argumentTypeMap.put("resource_or_tag_key_effect", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.MOB_EFFECT));
            argumentTypeMap.put("resource_or_tag_key_enchantment", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ENCHANTMENT));
            argumentTypeMap.put("resource_or_tag_key_biome", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.BIOME));
            argumentTypeMap.put("resource_or_tag_key_feature", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.FEATURE));
            argumentTypeMap.put("resource_or_tag_key_structure", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.STRUCTURE));

            argumentTypeMap.put("resource_location", ResourceLocationArgument.id());

            //Scoreboard Stuff
            argumentTypeMap.put("objective", ObjectiveArgument.objective());
            argumentTypeMap.put("objective_criteria", ObjectiveCriteriaArgument.criteria());
            argumentTypeMap.put("scoreboard_slot", ScoreboardSlotArgument.displaySlot());
            argumentTypeMap.put("score_holder", ScoreHolderArgument.scoreHolder());
            argumentTypeMap.put("score_holders", ScoreHolderArgument.scoreHolders());

            //Position Stuff
            argumentTypeMap.put("block_pos", BlockPosArgument.blockPos());
            argumentTypeMap.put("column_pos", ColumnPosArgument.columnPos());
            argumentTypeMap.put("vec2", Vec2Argument.vec2());
            argumentTypeMap.put("vec2_centered_integers", Vec2Argument.vec2(center_integers));
            argumentTypeMap.put("vec3", Vec3Argument.vec3());
            argumentTypeMap.put("vec3_centered_integers", Vec3Argument.vec3(center_integers));
            argumentTypeMap.put("rotation", RotationArgument.rotation());
            argumentTypeMap.put("swizzle", SwizzleArgument.swizzle());

            //World stuff
            argumentTypeMap.put("heightmap", HeightmapTypeArgument.heightmap());
            argumentTypeMap.put("dimension", DimensionArgument.dimension());
            argumentTypeMap.put("time", TimeArgument.time());

            //Game Tests
            argumentTypeMap.put("test_class", TestClassNameArgument.testClassName());
            argumentTypeMap.put("test_argument", TestFunctionArgument.testFunctionArgument());

            //Misc
            argumentTypeMap.put("particle_effect", ParticleArgument.particle(commandBuildContext));
            argumentTypeMap.put("color", ColorArgument.color());
            argumentTypeMap.put("function", FunctionArgument.functions());

            //Custom
            argumentTypeMap.put("mod_id", ModIdArgument.modIdArgument());

            return argumentTypeMap.get(type);
        }
    }

    public class LiteralNode extends Node {
    }

}
