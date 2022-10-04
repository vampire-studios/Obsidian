package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Commands implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Command.CommandNode command = Obsidian.GSON.fromJson(new FileReader(file), Command.CommandNode.class);
        String tmpl = """
                {
                  "name": "testing",
                  "op_level": 2,
                  "arguments": {
                    "target_pos" : {
                      "argument_type": "block_pos",
                      "executes": [
                        "tp @s {target_pos}"
                      ]
                    },
                    "user": {
                      "argument_type": "player",
                      "executes": [
                        "tp @s {user}"
                      ],
                      "arguments": {
                        "target": {
                          "argument_type": "player",
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
            json = readFileAsString(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            json = tmpl;
        }
        try {
            if (command == null || json.isEmpty()) return;
            String finalJson = json;

            Identifier identifier = Objects.requireNonNullElseGet(
                    command.name,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (command.name == null) command.name = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));

            CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> parseNodes(dispatcher, context, environment, finalJson));
            register(ContentRegistries.COMMANDS, "command", identifier, command);
        } catch (Exception e) {
            failedRegistering("command", file.getName(), e);
        }
    }

    public static String readFileAsString(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    @Override
    public String getType() {
        return "commands";
    }

    void parseNodes(CommandDispatcher<ServerCommandSource> dispatcher, CommandBuildContext buildContext, CommandManager.RegistrationEnvironment environment, String json) {
        Command.CommandNode node = Obsidian.GSON.fromJson(json, Command.CommandNode.class);
        if (node.dedicatedOnly) {
            if (environment.dedicated) {
                LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal(node.name.getPath());
                parse(root, buildContext, environment, node, new String[]{ });
                dispatcher.register(root);
            }
        } else {
            LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal(node.name.getPath());
            parse(root, buildContext, environment, node, new String[]{ });
            dispatcher.register(root);
        }
    }

    void parse(ArgumentBuilder<ServerCommandSource, ?> parent, CommandBuildContext buildContext, CommandManager.RegistrationEnvironment environment, Command.LiteralNode node, String name, String[] args) {
        LiteralArgumentBuilder<ServerCommandSource> _this = CommandManager.literal(name);
        parse(_this, buildContext, environment, node, args);
        parent.then(_this);
    }

    void parse(ArgumentBuilder<ServerCommandSource, ?> parent, CommandBuildContext buildContext, CommandManager.RegistrationEnvironment environment, Command.ArgumentNode node, String name, String[] args) {
        RequiredArgumentBuilder<ServerCommandSource, ?> _this = CommandManager.argument(name, node.getArgumentType(buildContext));
        parse(_this, buildContext, environment, node, args);
        parent.then(_this);
    }

    void parse(ArgumentBuilder<ServerCommandSource, ?> parent, CommandBuildContext buildContext, CommandManager.RegistrationEnvironment environment, Command.Node node, String[] args) {
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
                    ServerCommandSource source = ctx.getSource().withLevel(node.op_level);
                    source.getServer().getCommandManager()
                            .getDispatcher().execute(formatted, source);
                }
                return 0;
            });
        }
    }
}
