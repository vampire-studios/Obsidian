/*
package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandImpl {

    public static void register(Command command, CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> basenode = dispatcher.register(literal(command.name)).createBuilder();
        basenode.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(command.op_level));
        command.arguments.forEach((name, node) -> {
        });
        basenode.executes(context -> execute(context.getSource(), command));
    }

    private static int execute(ServerCommandSource source, Command command) {
        CommandFunctionManager commandFunctionManager = Objects.requireNonNull(MinecraftClient.getInstance().getServer()).getCommandFunctionManager();
        if (commandFunctionManager.getFunction(command.execute).isPresent()) {
            commandFunctionManager.execute(commandFunctionManager.getFunction(command.execute).get(), source);
        }
        return 1;
    }

}
*/
