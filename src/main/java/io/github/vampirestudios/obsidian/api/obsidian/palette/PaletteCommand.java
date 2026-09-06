package io.github.vampirestudios.obsidian.api.obsidian.palette;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.registry.components.PaletteComponent;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * {@code /obsidian palette …} — repaints the stack in the player's main hand.
 *
 * <p>Not a gameplay feature so much as the thing that makes palettes visible: without it an addon
 * author has no way to see a palette applied to an already-crafted item.</p>
 */
public final class PaletteCommand {

	private PaletteCommand() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> dispatcher.register(
				Commands.literal("obsidian").then(Commands.literal("palette")
						.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.then(Commands.literal("set")
								.then(Commands.argument("palette", IdentifierArgument.id())
										.suggests((context, builder) -> SharedSuggestionProvider.suggestResource(
												ContentRegistries.PALETTES.keySet(), builder))
										.executes(context -> set(context.getSource(),
												IdentifierArgument.getId(context, "palette")))))
						.then(Commands.literal("channel")
								.then(Commands.argument("channel", StringArgumentType.word())
										.then(Commands.argument("color", StringArgumentType.string())
												.executes(context -> override(context.getSource(),
														StringArgumentType.getString(context, "channel"),
														StringArgumentType.getString(context, "color"))))))
						.then(Commands.literal("clear")
								.executes(context -> clear(context.getSource())))
						.then(Commands.literal("info")
								.executes(context -> info(context.getSource()))))
		));
	}

	private static ItemStack heldStack(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		Player player = source.getPlayerOrException();
		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty()) source.sendFailure(Component.literal("You are not holding anything."));
		return stack;
	}

	private static int set(CommandSourceStack source, Identifier paletteId) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		ItemStack stack = heldStack(source);
		if (stack.isEmpty()) return 0;

		if (ContentRegistries.PALETTES.getValue(paletteId) == null) {
			source.sendFailure(Component.literal("Unknown palette: " + paletteId));
			return 0;
		}

		stack.set(OItemComponents.PALETTE, PaletteResolver.selectionOf(stack).withPalette(paletteId));
		source.sendSuccess(() -> Component.literal("Painted with " + paletteId), false);
		return 1;
	}

	private static int override(CommandSourceStack source, String channel, String color) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		ItemStack stack = heldStack(source);
		if (stack.isEmpty()) return 0;

		int argb = PaletteResolver.fromExpression(color, Integer.MIN_VALUE);
		if (argb == Integer.MIN_VALUE) {
			source.sendFailure(Component.literal("Not a colour: " + color));
			return 0;
		}

		stack.set(OItemComponents.PALETTE, PaletteResolver.selectionOf(stack).withOverride(channel, argb));
		source.sendSuccess(() -> Component.literal("Channel " + channel + " set to " + PaletteColor.toHex(argb)), false);
		return 1;
	}

	private static int clear(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		ItemStack stack = heldStack(source);
		if (stack.isEmpty()) return 0;

		stack.remove(OItemComponents.PALETTE);
		source.sendSuccess(() -> Component.literal("Palette cleared."), false);
		return 1;
	}

	private static int info(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
		ItemStack stack = heldStack(source);
		if (stack.isEmpty()) return 0;

		PaletteComponent selection = PaletteResolver.selectionOf(stack);
		source.sendSuccess(() -> Component.literal("Palette: "
				+ selection.palette().map(Identifier::toString).orElse("<none>")), false);

		for (Map.Entry<String, Integer> override : selection.overrides().entrySet()) {
			source.sendSuccess(() -> Component.literal("  " + override.getKey()
					+ " → " + PaletteColor.toHex(override.getValue())), false);
		}
		return 1;
	}
}
