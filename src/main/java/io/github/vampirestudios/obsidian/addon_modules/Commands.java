package io.github.vampirestudios.obsidian.addon_modules;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Commands implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Command.CommandNode command = loadCommandFromFile(addon, file);
		String tmpl = """
				{
				  "name": "testing",
				  "aliases": [
				    "testing2"
				  ]
				  "permission_level": 2,
				  "arguments": {
				    "target_pos" : {
				      "type": "block_pos",
				      "executes": [
				        "tp @s {target_pos}"
				      ]
				    },
				    "user": {
				      "type": "player",
				      "executes": [
				        "tp @s {user}"
				      ],
				      "arguments": {
				        "target": {
				          "type": "player",
				          "executes": [
				            "tp {user} {target}"
				          ]
				        }
				      }
				    }
				  }
				}
				""";
		String json;
		try {
			json = AddonFormats.readTree(addon, file).toString();
		} catch (Exception e) {
			e.printStackTrace();
			json = tmpl;
		}
		try {
			if (command == null || json.isEmpty()) return;
			String finalJson = json;

			Identifier identifier = getCommandIdentifier(command, id, file);
			if (command.name == null)
				command.name = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));

			CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> parseNodes(dispatcher, context, environment, finalJson));
			register(ContentRegistries.COMMANDS, "command", identifier, command);
		} catch (Exception e) {
			failedRegistering("command", file.getName(), e);
		}
	}

//    private void registerCommand(Command.CommandNode command, Identifier identifier) {
//        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) ->
//                parseNodes(dispatcher, context, environment, command));
//        register(ContentRegistries.COMMANDS, "command", identifier, command);
//    }

	private Command.CommandNode loadCommandFromFile(IAddonPack addon, File file) throws IOException {
		return AddonFormats.read(addon, file, Command.CommandNode.class);
	}

	private Identifier getCommandIdentifier(Command.CommandNode command, BasicAddonInfo id, File file) {
		return Objects.requireNonNullElseGet(command.name,
				() -> Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file)));
	}

	public static String readFileAsString(String file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file)));
	}

	@Override
	public String getType() {
		return "command";
	}

	/**
	 * The text the player typed for each argument in scope, keyed by argument name.
	 *
	 * <p>Read back from the input string rather than from the parsed value. {@code getArgument} needs the
	 * exact class the argument type produces, and only string-shaped types produce a {@link String} — asking
	 * for one from a {@code block_pos} or an {@code entity} throws at execution time. What an
	 * {@code executes} line wants is the text anyway: {@code tp @s {target}} needs the words the player
	 * wrote, not a parsed {@code Coordinates}. Taking the input substring works for every argument type
	 * and round-trips exactly.
	 */
	private static Map<String, String> typedArguments(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx,
	                                                  String[] args) {
		Set<String> inScope = new HashSet<>(Arrays.asList(args));
		Map<String, String> arguments = new HashMap<>();
		String input = ctx.getInput();

		for (ParsedCommandNode<CommandSourceStack> parsed : ctx.getNodes()) {
			if (!(parsed.getNode() instanceof ArgumentCommandNode<?, ?> argument)) continue;

			String name = argument.getName();
			if (!inScope.contains(name)) continue;

			// Ranges are absolute offsets into the whole command, so this stays correct for a command
			// reached through an alias redirect.
			arguments.put(name, parsed.getRange().get(input));
		}
		return arguments;
	}

	void parseNodes(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, net.minecraft.commands.Commands.CommandSelection environment, String json) {
		Command.CommandNode node = BaseGson.GSON.fromJson(json, Command.CommandNode.class);
		if (node.dedicatedOnly) {
			if (environment.includeDedicated) {
				LiteralArgumentBuilder<CommandSourceStack> root = net.minecraft.commands.Commands.literal(node.name.getPath());
				parse(root, buildContext, environment, node, new String[]{});
				LiteralCommandNode<CommandSourceStack> registered = dispatcher.register(root);
				if (node.aliases != null) {
					for (String alias : node.aliases) {
						dispatcher.register(net.minecraft.commands.Commands.literal(alias).redirect(registered));
					}
				}
			}
		} else {
			LiteralArgumentBuilder<CommandSourceStack> root = net.minecraft.commands.Commands.literal(node.name.getPath());
			parse(root, buildContext, environment, node, new String[]{});
			LiteralCommandNode<CommandSourceStack> registered = dispatcher.register(root);
			if (node.aliases != null) {
				for (String alias : node.aliases) {
					dispatcher.register(net.minecraft.commands.Commands.literal(alias).redirect(registered));
				}
			}
		}
	}

	void parse(ArgumentBuilder<CommandSourceStack, ?> parent, CommandBuildContext buildContext, net.minecraft.commands.Commands.CommandSelection environment, Command.LiteralNode node, String name, String[] args) {
		LiteralArgumentBuilder<CommandSourceStack> _this = net.minecraft.commands.Commands.literal(name);
		parse(_this, buildContext, environment, node, args);
		parent.then(_this);
	}

	void parse(ArgumentBuilder<CommandSourceStack, ?> parent, CommandBuildContext buildContext, net.minecraft.commands.Commands.CommandSelection environment, Command.ArgumentNode node, String name, String[] args) {
		RequiredArgumentBuilder<CommandSourceStack, ?> _this = net.minecraft.commands.Commands.argument(name, node.getArgumentType(buildContext));
		parse(_this, buildContext, environment, node, args);
		parent.then(_this);
	}

	void parse(ArgumentBuilder<CommandSourceStack, ?> parent, CommandBuildContext buildContext, net.minecraft.commands.Commands.CommandSelection environment, Command.Node node, String[] args) {
		if (node.arguments != null) {
			node.arguments.forEach((_name, _node) -> {
				ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
				list.add(_name);
				parse(parent, buildContext, environment, _node, _name, list.toArray(new String[0]));
			});
		}
		if (node.literals != null) {
			node.literals.forEach((_name, _node) -> parse(parent, buildContext, environment, _node, _name, args));
		}
		if (node.op_level != null) {
			parent.requires(net.minecraft.commands.Commands.hasPermission(node.getPermissionFromInt()));
		}
		if (node.executes != null) {
			parent.executes((ctx) -> {
				Map<String, String> arguments = typedArguments(ctx, args);

				StrSubstitutor sub = new StrSubstitutor(arguments, "{", "}");

				for (String command : node.executes) {
					String formatted = sub.replace(command);
					CommandSourceStack source = ctx.getSource().withPermission(node.getPermissionSetFromInt());
					source.getServer().getCommands()
							.getDispatcher().execute(formatted, source);
				}
				return 0;
			});
		}
	}
}
