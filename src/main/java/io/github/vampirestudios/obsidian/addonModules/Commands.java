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
import net.minecraft.server.command.ServerCommandSource;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Commands implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Command.Node command = Obsidian.GSON.fromJson(new FileReader(file), Command.Node.class);
        String tmpl = """
                {
                  "command_name": "tp_copy",
                  "oplevel": 2,
                  "arguments": {
                    "target_pos" : {
                      "argumentType": "block_pos",
                      "execute": [
                        "tp @s {target_pos}"
                      ]
                    },
                    "user": {
                      "argumentType": "player",
                      "executes": [
                        "tp @s {user}"
                      ],
                      "arguments": {
                        "target": {
                          "argumentType": "player",
                          "execute": [
                            "tp {user} {target}"
                          ]
                        }
                      }
                    }
                  }
                }
                """;
//        String commandAsString = Obsidian.GSON.fromJson(new FileReader(file), String.class);
        try {
            if (command == null) return;
            // Using a lambda
            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                // This command will be registered regardless of the server being dedicated or integrated
//                CommandImpl.register(command, dispatcher);
                parseNodes(dispatcher, tmpl);
            });
            register(COMMANDS, "command", command.command_name, command);
        } catch (Exception e) {
            failedRegistering("command", command.command_name, e);
        }
    }

    @Override
    public String getType() {
        return "commands";
    }

    void parseNodes(CommandDispatcher<ServerCommandSource> dispatcher, String json) {
        Command.CommandNode node = Obsidian.GSON.fromJson(json, Command.CommandNode.class);
        LiteralArgumentBuilder<ServerCommandSource> root = LiteralArgumentBuilder.literal(node.name);
        parse(root, node, new String[]{ });
        dispatcher.register(root);
    }

    void parse(ArgumentBuilder<ServerCommandSource, ?> parent, Command.LiteralNode node, String name, String[] args) {
        LiteralArgumentBuilder<ServerCommandSource> _this = LiteralArgumentBuilder.literal(name);
        parse(_this, node, args);
        parent.then(_this);
    }

    void parse(ArgumentBuilder<ServerCommandSource, ?> parent, Command.ArgumentNode node, String name, String[] args) {
        RequiredArgumentBuilder<ServerCommandSource, ?> _this = RequiredArgumentBuilder.argument(name, node.getArgumentType());
        parse(_this, node, args);
        parent.then(_this);
    }

    void parse(ArgumentBuilder<ServerCommandSource, ?> parent, Command.Node node, String[] args) {
        if (node.arguments != null) {
            node.arguments.forEach((_name, _node) -> {
                List<String> list = Arrays.asList(args);
                list.add(_name);
                parse(parent, _node, _name, list.toArray(new String[0]));
            });
        }
        if (node.literals != null) {
            node.literals.forEach((_name, _node) -> parse(parent, _node, _name, args));
        }
        if (node.oplevel != null) {
            parent.requires((ctx) -> ctx.hasPermissionLevel(node.oplevel));
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
                    System.out.println("Running: " + formatted);
                    // TODO: Run `formatted` without permission checks
                    // Maybe with a custom ServerCommandSource that always has permission level 4?
                }
                return 0;
            });
        }
    }
}
