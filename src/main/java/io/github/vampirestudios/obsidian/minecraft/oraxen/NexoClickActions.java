package io.github.vampirestudios.obsidian.minecraft.oraxen;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Locale;

/** Shared Nexo click-action execution for furniture and custom blocks. */
public final class NexoClickActions {
	private NexoClickActions() {
	}

	public static boolean run(Level level, BlockPos pos, Player player,
	                          List<NexoItem.Mechanics.Furniture.ClickAction> clickActions) {
		if (level.isClientSide() || clickActions == null || level.getServer() == null) return false;
		boolean handled = false;
		for (NexoItem.Mechanics.Furniture.ClickAction clickAction : clickActions) {
			if (clickAction == null || clickAction.actions == null
					|| !NexoExpressionConditions.passAll(level.getServer(), player, clickAction.conditions)) continue;
			for (String encoded : clickAction.actions) {
				if (execute(level, pos, player, encoded)) handled = true;
			}
		}
		return handled;
	}

	private static boolean execute(Level level, BlockPos pos, Player player, String encoded) {
		if (encoded == null || encoded.isBlank()) return false;
		String action = encoded.replace("<player>", player.getScoreboardName()).trim();
		String options = null;
		if (action.startsWith("{")) {
			int end = action.indexOf('}');
			if (end > 0) {
				options = action.substring(1, end);
				action = action.substring(end + 1).trim();
			}
		}

		if (action.startsWith("[console]")) {
			String command = action.substring("[console]".length()).trim();
			level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack(), command);
			return true;
		}
		if (action.startsWith("[player]")) {
			String command = action.substring("[player]".length()).trim();
			level.getServer().getCommands().performPrefixedCommand(
					player.createCommandSourceStackForNameResolution((ServerLevel) player.level()), command);
			return true;
		}
		if (action.startsWith("[message]")) {
			player.sendSystemMessage(TagParser.QUICK_TEXT
					.parseNode(action.substring("[message]".length()).trim()).toComponent());
			return true;
		}
		if (action.startsWith("[actionbar]")) {
			player.sendOverlayMessage(TagParser.QUICK_TEXT
					.parseNode(action.substring("[actionbar]".length()).trim()).toComponent());
			return true;
		}
		if (action.startsWith("[sound]")) {
			return playSound(level, pos, player, action.substring("[sound]".length()).trim(), options);
		}

		Obsidian.LOGGER.warn("Unknown Nexo click action '{}'", encoded);
		return false;
	}

	private static boolean playSound(Level level, BlockPos pos, Player player, String soundName, String options) {
		Identifier soundId = Identifier.tryParse(soundName);
		if (soundId == null) return false;
		var sound = BuiltInRegistries.SOUND_EVENT.getOptional(soundId).orElse(null);
		if (sound == null) return false;

		SoundSource source = SoundSource.BLOCKS;
		float volume = 1.0F;
		float pitch = 1.0F;
		boolean self = false;
		if (options != null) {
			for (String option : options.trim().split("\\s+")) {
				String[] pair = option.split("=", 2);
				if (pair.length != 2) continue;
				try {
					switch (pair[0].toLowerCase(Locale.ROOT)) {
						case "source" -> source = SoundSource.valueOf(pair[1].toUpperCase(Locale.ROOT));
						case "volume" -> volume = Float.parseFloat(pair[1]);
						case "pitch" -> pitch = Float.parseFloat(pair[1]);
						case "self" -> self = Boolean.parseBoolean(pair[1]);
					}
				} catch (IllegalArgumentException exception) {
					Obsidian.LOGGER.warn("Ignoring malformed Nexo sound option '{}'", option);
				}
			}
		}
		level.playSound(null, self ? player.blockPosition() : pos, sound, source, volume, pitch);
		return true;
	}
}
