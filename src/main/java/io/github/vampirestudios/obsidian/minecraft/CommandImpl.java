package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.vampirestudios.obsidian.api.obsidian.command.Argument;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandImpl {

    public static void register(Command command, CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> basenode = dispatcher.register(literal(command.name)).createBuilder();
        for (Argument argument : command.arguments) {
            basenode.then(CommandManager.argument(argument.name, argument.getBasicArgumentType()));
        }
    }

    private static int execute(ServerCommandSource source, Command command) {
        source.sendFeedback(new LiteralText(command.name), false);
        return 1;
    }

}
