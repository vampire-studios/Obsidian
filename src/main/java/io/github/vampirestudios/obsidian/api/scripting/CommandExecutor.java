package io.github.vampirestudios.obsidian.api.scripting;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Prediction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandExecutor {
	public boolean execute(String line, Player player, Level world, BlockPos pos,
	                       ScriptManager.FlowController flow) {
		String[] parts = line.split("\\s+");
		switch (parts[0]) {
			case "send" -> {
				// send "<msg>"
				String msg = stripQuotes(line.substring(4).trim())
						.replace("%player%", player != null ? player.getName().getString() : "");
				if (player != null) player.sendOverlayMessage(Component.literal(msg));
			}
			case "actionbar" -> {
				// actionbar "<msg>"
				String msg = stripQuotes(line.substring(9).trim());
				if (player != null) player.sendOverlayMessage(Component.literal(msg));
			}
			case "title" -> {
				// title "<title>" [subtitle "<subtitle>"]
				Matcher tm = Pattern.compile(
						"^title\\s+\"([^\"]+)\"(?:\\s+subtitle\\s+\"([^\"]+)\")?").matcher(line);
				if (tm.find() && player instanceof ServerPlayer sp) {
					String titleText = tm.group(1);
					String subtitleText = tm.group(2);
					sp.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
					sp.connection.send(new ClientboundSetTitleTextPacket(Component.literal(titleText)));
					if (subtitleText != null) {
						sp.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(subtitleText)));
					}
				}
			}
			case "sound" -> {
				// sound <id> [<volume>] [<pitch>]
				// e.g.  sound minecraft:entity.experience_orb.pickup 1.0 1.0
				if (world != null && player != null) {
					float vol = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
					float pitch = parts.length > 3 ? Float.parseFloat(parts[3]) : 1.0f;
					var soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.tryParse(parts[1]));
					if (soundEvent != null) {
						world.playSound(null, player.blockPosition(), soundEvent, SoundSource.MASTER, vol, pitch);
					}
				}
			}
			case "effect" -> {
				// effect <id> <duration_seconds> [amplifier]
				// e.g.  effect minecraft:speed 30 1
				if (player != null && parts.length >= 3) {
					int duration = Integer.parseInt(parts[2]);
					int amp = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
					BuiltInRegistries.MOB_EFFECT.get(Identifier.tryParse(parts[1]))
							.ifPresent(h -> player.addEffect(new MobEffectInstance(h, duration * 20, amp)));
				}
			}
			case "teleport", "tp" -> {
				// teleport <x> <y> <z>
				if (player != null && parts.length >= 4) {
					double tx = Double.parseDouble(parts[1]);
					double ty = Double.parseDouble(parts[2]);
					double tz = Double.parseDouble(parts[3]);
					player.teleportTo(tx, ty, tz);
				}
			}
			case "kick" -> {
				// kick ["<reason>"]
				String reason = parts.length > 1 ? stripQuotes(line.substring(4).trim()) : "Kicked by server";
				if (player instanceof ServerPlayer sp) {
					sp.connection.disconnect(Component.literal(reason));
				}
			}
			case "give" -> {
				if (player == null) break;

				// Special case: give xp <amount>
				if (parts.length >= 3 && parts[1].equalsIgnoreCase("xp")) {
					player.giveExperiencePoints(Integer.parseInt(parts[2]));
					break;
				}

				// Regex (case‐insensitive) with groups:
				// 1=count, 2=itemId, 3=name, 4=singleLore, 5=listLore, 6=cmdl
				Pattern p = Pattern.compile(
						"^(?i)(?:give)\\s+"                    // command
								+ "(\\d+)\\s+"                         // (1) count
								+ "(?:of\\s+)?([\\w:]+)"               // (2) itemId
								+ "(?:\\s+named\\s+\"([^\"]+)\")?"     // (3) name
								+ "(?:\\s+with\\s+lore\\s+"            // lore clause
								+ "(?:\"([^\"]+)\"|\\[([^]]+)])"       // (4) single or (5) comma-list
								+ ")?"
								+ "(?:\\s+with\\s+custom\\s+model\\s+data\\s+(\\d+))?" // (6) cmdl
				);
				Matcher m = p.matcher(line);
				if (!m.find()) {
					player.sendOverlayMessage(Component.literal("[Obs] Invalid syntax: " + line));
					break;
				}

				int count = Integer.parseInt(m.group(1));
				String itemId = m.group(2);
				String name = m.group(3);
				String singleLore = m.group(4);
				String listLore = m.group(5);
				String cmdlStr = m.group(6);
				Integer cmdl = cmdlStr != null ? Integer.parseInt(cmdlStr) : null;

				List<String> lore = new ArrayList<>();
				if (singleLore != null) {
					lore.add(singleLore);
				} else if (listLore != null) {
					for (String s : listLore.split("\\s*,\\s*")) {
						lore.add(stripQuotes(s.trim()));
					}
				}

				Identifier rl = Identifier.tryParse(itemId);
				ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getValue(rl), count);

				if (name != null) {
					stack.set(DataComponents.ITEM_NAME, Component.literal(name));
				}
				if (!lore.isEmpty()) {
					stack.set(DataComponents.LORE, new ItemLore(lore.stream()
							.map(s -> (Component) Component.literal(s))
							.toList()));
				}
				if (cmdl != null) {
					stack.set(DataComponents.CUSTOM_MODEL_DATA,
							new CustomModelData(List.of(Float.valueOf(cmdl)), List.of(), List.of(), List.of()));
				}

				if (!player.getInventory().add(stack)) player.drop(stack, false, Prediction.PREDICTED);
			}
			case "drop" -> {
				if (player == null) break;

				Pattern p = Pattern.compile(
						"^(?i)(?:drop)\\s+"
								+ "(\\d+)\\s+"
								+ "(?:of\\s+)?([\\w:]+)"
								+ "(?:\\s+named\\s+\"([^\"]+)\")?"
								+ "(?:\\s+with\\s+lore\\s+"
								+ "(?:\"([^\"]+)\"|\\[([^]]+)]))"
								+ "?(?:\\s+with\\s+custom\\s+model\\s+data\\s+(\\d+))?"
				);
				Matcher m = p.matcher(line);
				if (!m.find()) {
					player.sendOverlayMessage(Component.literal("[Obs] Invalid syntax: " + line));
					break;
				}

				int count = Integer.parseInt(m.group(1));
				String itemId = m.group(2);
				String name = m.group(3);
				String singleLore = m.group(4);
				String listLore = m.group(5);
				String cmdlStr = m.group(6);
				Integer cmdl = cmdlStr != null ? Integer.parseInt(cmdlStr) : null;

				List<String> lore = new ArrayList<>();
				if (singleLore != null) lore.add(singleLore);
				else if (listLore != null)
					for (String s : listLore.split("\\s*,\\s*")) lore.add(stripQuotes(s.trim()));

				Identifier rl = Identifier.tryParse(itemId);
				ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getValue(rl), count);
				if (name != null) stack.set(DataComponents.ITEM_NAME, Component.literal(name));
				if (!lore.isEmpty()) {
					stack.set(DataComponents.LORE, new ItemLore(lore.stream()
							.map(s -> (Component) Component.literal(s)).toList()));
				}
				if (cmdl != null) {
					stack.set(DataComponents.CUSTOM_MODEL_DATA,
							new CustomModelData(List.of(Float.valueOf(cmdl)), List.of(), List.of(), List.of()));
				}
				player.drop(stack, false, Prediction.PREDICTED);
			}
			case "broadcast" -> {
				// broadcast "<msg>"
				String msg1 = stripQuotes(line.substring(9)).trim();
				if (world != null && world.getServer() != null) {
					world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(msg1), false);
				}
			}
			case "console" -> {
				// console "<cmd>"
				String cmd = stripQuotes(line.substring(7));
				if (world != null) {
					var css = world.getServer().createCommandSourceStack();
					try {
						css.dispatcher().execute(cmd, css);
					} catch (CommandSyntaxException e) {
						e.fillInStackTrace();
					}
				}
			}
			case "set" -> {
				// set time <n>          — change world time
				// set weather <type>    — clear | rain | thunder
				// set hunger <n>        — set player food level
				// set health <n>        — set player health
				// set level <n>         — set player XP level
				if (parts.length < 3) break;
				switch (parts[1]) {
					case "time" -> {
						if (world instanceof ServerLevel sw)
							sw.clockManager().setTotalTicks(sw.dimensionType().defaultClock().orElseThrow(), Long.parseLong(parts[2]));
					}
					case "weather" -> {
						if (world != null && world.getServer() != null) {
							try {
								var css = world.getServer().createCommandSourceStack();
								css.dispatcher().execute("weather " + parts[2], css);
							} catch (CommandSyntaxException ignored) {
							}
						}
					}
					case "hunger" -> {
						if (player != null)
							player.getFoodData().setFoodLevel(Integer.parseInt(parts[2]));
					}
					case "health" -> {
						if (player != null)
							player.setHealth(Float.parseFloat(parts[2]));
					}
					case "level" -> {
						if (player != null) {
							int target = Integer.parseInt(parts[2]);
							player.giveExperienceLevels(target - player.experienceLevel);
						}
					}
					default -> System.err.println("[Obs] Unknown set target: " + parts[1]);
				}
			}
			case "wait" -> {
				// wait <n> [ticks|seconds|minutes]  (default: seconds)
				int n = Integer.parseInt(parts[1]);
				int ticks;
				if (parts.length > 2) {
					ticks = switch (parts[2].toLowerCase()) {
						case "tick", "ticks" -> n;
						case "second", "seconds" -> n * 20;
						case "minute", "minutes" -> n * 20 * 60;
						default -> n * 20;
					};
				} else {
					ticks = n * 20;
				}
				flow.delay(ticks);
				return false;
			}
			default -> System.err.println("Unknown command: " + line);
		}
		return true;
	}

	public String stripQuotes(String s) {
		if ((s.startsWith("\"") && s.endsWith("\"")) ||
				(s.startsWith("'") && s.endsWith("'"))) {
			return s.substring(1, s.length() - 1);
		}
		return s;
	}
}
