package io.github.vampirestudios.obsidian.addonapi.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.vampirestudios.obsidian.addonapi.command.CommandArgumentDef;
import io.github.vampirestudios.obsidian.addonapi.command.CommandDefinition;
import io.github.vampirestudios.obsidian.addonapi.menu.MenuDefinition;
import io.github.vampirestudios.obsidian.addonapi.model.ActionDefinition;
import io.github.vampirestudios.obsidian.addonapi.registry.AddonCommandRegistry;
import io.github.vampirestudios.obsidian.addonapi.registry.AddonMenuRegistry;
import io.github.vampirestudios.obsidian.addonapi.registry.MenuTypeRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class AddonCommands {

	public static void register(
			CommandDispatcher<CommandSourceStack> dispatcher,
			CommandBuildContext ctx,
			Commands.CommandSelection env
	) {
		for (CommandDefinition def : AddonCommandRegistry.all()) {
			if (def.path == null || def.path.isEmpty()) continue;

			LiteralArgumentBuilder<CommandSourceStack> root =
					Commands.literal(def.path.getFirst())
							.requires(Commands.hasPermission(def.getPermissionFromInt()));

			LiteralArgumentBuilder<CommandSourceStack> current = root;

			for (int i = 1; i < def.path.size(); i++) {
				LiteralArgumentBuilder<CommandSourceStack> child =
						Commands.literal(def.path.get(i));
				current.then(child);
				current = child;
			}

			// Attach args to last literal
			var argTail = attachArguments(current, def.arguments, ctx);
			if (argTail == null) {
				current.executes(c -> execute(def, c));
			} else {
				argTail.executes(c -> execute(def, c));
			}

			dispatcher.register(root);
		}
	}

	private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, ?> attachArguments(
			LiteralArgumentBuilder<CommandSourceStack> base,
			List<CommandArgumentDef> args,
			CommandBuildContext ctx
	) {
		if (args == null || args.isEmpty()) return null;

		com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, ?> head = null;
		com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, ?> current = null;

		for (CommandArgumentDef def : args) {
			var arg = switch (def.type) {
				case "player" -> Commands.argument(def.name, EntityArgument.player());
				case "integer" ->
						Commands.argument(def.name, com.mojang.brigadier.arguments.IntegerArgumentType.integer());
				case "string" ->
						Commands.argument(def.name, com.mojang.brigadier.arguments.StringArgumentType.string());
				default -> Commands.argument(def.name, com.mojang.brigadier.arguments.StringArgumentType.word());
			};

			if (head == null) {
				head = arg;
				base.then(head);
			} else {
				current.then(arg);
			}
			current = arg;
		}

		return current;
	}

	private static int execute(CommandDefinition def, CommandContext<CommandSourceStack> ctx)
			throws com.mojang.brigadier.exceptions.CommandSyntaxException {

		ServerPlayer self = ctx.getSource().getPlayerOrException();

		if (def.execute != null) {
			for (ActionDefinition action : def.execute) {
				performAction(action, def, ctx, self);
			}
		}
		return 1;
	}

	public static void performAction(
			ActionDefinition action,
			CommandDefinition def,
			CommandContext<CommandSourceStack> ctx,
			ServerPlayer self
	) throws com.mojang.brigadier.exceptions.CommandSyntaxException {

		switch (action.action) {
			case "run_command" -> {
				if (action.command != null && !action.command.isEmpty()) {
					ctx.getSource().getServer().getCommands()
							.performPrefixedCommand(ctx.getSource(), action.command);
				}
			}
			case "send_message" -> {
				Component msg = action.message != null
						? action.message.toMc()
						: Component.literal("No message configured");
				ctx.getSource().sendSuccess(() -> msg, false);
			}
			case "open_menu" -> {
				if (action.menu == null) return;
				Identifier id = Identifier.parse(action.menu);
				MenuDefinition menu = AddonMenuRegistry.get(id);
				if (menu == null) {
					ctx.getSource().sendFailure(Component.literal("Unknown menu: " + id));
					return;
				}
				ServerPlayer target = resolveTarget(action, ctx, self);
				if (target != null) {
					MenuTypeRegistry.open(target, menu);
				}
			}
			case "close_menu" -> self.closeContainer();
			case "client_quit" -> self.connection.disconnect(Component.literal("Quit from JSON command"));
		}
	}

	private static ServerPlayer resolveTarget(
			ActionDefinition action,
			CommandContext<CommandSourceStack> ctx,
			ServerPlayer self
	) throws com.mojang.brigadier.exceptions.CommandSyntaxException {

		if (action.target == null || "self".equals(action.target)) return self;

		if (action.target.startsWith("arg:")) {
			String name = action.target.substring("arg:".length());
			Object arg = ctx.getArgument(name, Object.class);

			if (arg instanceof ServerPlayer p) return p;
			if (arg instanceof Entity e && e instanceof ServerPlayer p) return p;
		}

		return action.fallback_to_self ? self : null;
	}
}
