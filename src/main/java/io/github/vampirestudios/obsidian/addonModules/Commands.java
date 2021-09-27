package io.github.vampirestudios.obsidian.addonModules;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Commands implements AddonModule {
	public static String readFileAsString(String file) throws IOException {
		return new String(Files.readAllBytes(Paths.get(file)));
	}

	@Override
	public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
		Command.CommandNode command = Obsidian.GSON.fromJson(new FileReader(file), Command.CommandNode.class);
		String tmpl = "{\n" +
				"                  \"name\": \"testing\",\n" +
				"                  \"op_level\": 2,\n" +
				"                  \"arguments\": {\n" +
				"                    \"target_pos\" : {\n" +
				"                      \"argumentType\": \"block_pos\",\n" +
				"                      \"executes\": [\n" +
				"                        \"tp @s {target_pos}\"\n" +
				"                      ]\n" +
				"                    },\n" +
				"                    \"user\": {\n" +
				"                      \"argumentType\": \"player\",\n" +
				"                      \"executes\": [\n" +
				"                        \"tp @s {user}\"\n" +
				"                      ],\n" +
				"                      \"arguments\": {\n" +
				"                        \"target\": {\n" +
				"                          \"argumentType\": \"player\",\n" +
				"                          \"executes\": [\n" +
				"                            \"tp {user} {target}\"\n" +
				"                          ]\n" +
				"                        }\n" +
				"                      }\n" +
				"                    }\n" +
				"                  }\n" +
				"                }";
		String json;
		try {
			json = readFileAsString(file.getPath());
		} catch (IOException e) {
			e.printStackTrace();
			json = tmpl;
		}
		try {
			if (command == null || json.isEmpty()) return;
			String finalJson = json;
			CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> parseNodes(dispatcher, finalJson));
			register(COMMANDS, "command", command.name, command);
		} catch (Exception e) {
			failedRegistering("command", command.name.toString(), e);
		}
	}

	@Override
	public String getType() {
		return "commands";
	}

	void parseNodes(CommandDispatcher<ServerCommandSource> dispatcher, String json) {
		Command.CommandNode node = Obsidian.GSON.fromJson(json, Command.CommandNode.class);
		LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal(node.name.getPath());
		parse(root, node, new String[]{});
		dispatcher.register(root);
	}

	void parse(ArgumentBuilder<ServerCommandSource, ?> parent, Command.LiteralNode node, String name, String[] args) {
		LiteralArgumentBuilder<ServerCommandSource> _this = CommandManager.literal(name);
		parse(_this, node, args);
		parent.then(_this);
	}

	void parse(ArgumentBuilder<ServerCommandSource, ?> parent, Command.ArgumentNode node, String name, String[] args) {
		RequiredArgumentBuilder<ServerCommandSource, ?> _this = CommandManager.argument(name, node.getArgumentType());
		parse(_this, node, args);
		parent.then(_this);
	}

	void parse(ArgumentBuilder<ServerCommandSource, ?> parent, Command.Node node, String[] args) {
		if (node.arguments != null) {
			node.arguments.forEach((_name, _node) -> {
				ArrayList<String> list = new ArrayList<>(Arrays.asList(args));
				list.add(_name);
				parse(parent, _node, _name, list.toArray(new String[0]));
			});
		}
		if (node.literals != null) {
			node.literals.forEach((_name, _node) -> parse(parent, _node, _name, args));
		}
		if (node.op_level != null) {
			parent.requires((ctx) -> ctx.hasPermissionLevel(node.op_level));
		}
		if (node.executes != null) {
			parent.executes((ctx) -> {
				Map<String, String> arguments = new HashMap<>();
				for (String arg : args) {
					arguments.put(arg, ctx.getArgument(arg, String.class));
				}

				StrSubstitutor sub = new StrSubstitutor(arguments, "{", "}");

				for (String command : node.executes) {
					String formatted = sub.replace(command);
					ServerCommandSource source = ctx.getSource().withLevel(4);
					source.getMinecraftServer().getCommandManager()
							.getDispatcher().execute(formatted, source);
				}
				return 0;
			});
		}
	}
}
