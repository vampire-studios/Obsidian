package io.github.vampirestudios.obsidian.configPack;

import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import com.google.common.base.Joiner;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.RegistryHelper;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.scripting.ScriptManager;
import io.github.vampirestudios.obsidian.api.scripting.ScriptParser;
import io.github.vampirestudios.obsidian.api.scripting.ScriptParser.ParamDef;
import io.github.vampirestudios.obsidian.network.ContentPackSyncManager;
import io.github.vampirestudios.obsidian.network.ContentPackSyncNetworking;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.arguments.coordinates.*;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ObsidianAddonLoader {
	public static final File OBSIDIAN_ADDON_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("obsidian_addons").toFile();
	public static final File SERVER_OBSIDIAN_ADDON_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("server_obsidian_addons").toFile();
	public static final Registry<IAddonPack> OBSIDIAN_ADDONS = FabricRegistryBuilder.create(IAddonPack.class, Const.id("obsidian_addons")).buildAndRegister();
	public static final int SCHEMA_VERSION = 5;
	private static final Map<String, ScriptManager> managers = new HashMap<>();
	private static final Map<String, io.github.vampirestudios.obsidian.scripting.std.ObsPackRuntime> OBS_PACKS = new HashMap<>();
	public static RegistryHelper REGISTRY_HELPER;

	public static void loadDefaultObsidianAddons() {
		if (!OBSIDIAN_ADDON_DIRECTORY.exists())
			createObsidianAddonsFolder();
		if (!SERVER_OBSIDIAN_ADDON_DIRECTORY.exists())
			SERVER_OBSIDIAN_ADDON_DIRECTORY.mkdirs();
	}

	public static void loadServerObsidianAddons() {
		File[] entries = SERVER_OBSIDIAN_ADDON_DIRECTORY.listFiles();
		if (entries == null || entries.length == 0) return;
		for (File hashDir : entries) {
			register(hashDir, "addon.info.pack", "addon.info.json5");
		}
	}

	public static void register(File file, String legacyFile, String newFile) {
		if (file.isDirectory()) {
			try {
				File legacyPackInfoFile = new File(file, legacyFile);
				File newPackInfoFile = new File(file, newFile);
				Optional<File> fabricModJson;
				if (new File(file, "fabric.mod.json").exists())
					fabricModJson = Optional.of(new File(file, "fabric.mod.json"));
				else fabricModJson = Optional.empty();
				Utils.registerAddon(legacyPackInfoFile, newPackInfoFile, fabricModJson);
			} catch (Exception e) {
				Obsidian.LOGGER.error("Failed to load obsidian addon!", e);
			}
		} else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
			try (ZipFile zipFile = new ZipFile(file)) {
				ZipEntry packInfoEntry = zipFile.getEntry(legacyFile);
				ZipEntry packInfoEntry1 = zipFile.getEntry(newFile);
                /*if (packInfoEntry != null) {
                    Utils.registerAddon(new InputStreamReader(zipFile.getInputStream(packInfoEntry)));
                } else if (packInfoEntry1 != null) {
                    Utils.registerAddon(null, new InputStreamReader(zipFile.getInputStream(packInfoEntry1)));
                }*/
			} catch (Exception e) {
				Obsidian.LOGGER.error("Failed to load obsidian addon from zip!", e);
			}
		}
	}

	private static List<Path> findObsRoots(Path packRoot, String namespace) {
		List<Path> roots = new ArrayList<>();
		// preferred canonical folder names (pick your favorite and remove others)
		Path r1 = packRoot.resolve("obs");
		Path r2 = packRoot.resolve("scripts");
		Path r3 = packRoot.resolve("content").resolve(namespace).resolve("obs");
		if (java.nio.file.Files.isDirectory(r1)) roots.add(r1);
		if (java.nio.file.Files.isDirectory(r2)) roots.add(r2);
		if (java.nio.file.Files.isDirectory(r3)) roots.add(r3);
		return roots;
	}

	public static void loadObsidianAddons() {
		ContentPackSyncManager.reset();
		for (File file : Objects.requireNonNull(OBSIDIAN_ADDON_DIRECTORY.listFiles())) {
			// Load Packs
			register(file, "addon.info.pack", "addon.info.json5");
		}

		String moduleText;
		if (OBSIDIAN_ADDONS.keySet().size() > 1) {
			moduleText = "Loading {} obsidian addons:";
		} else {
			moduleText = "Loading {} obsidian addon:";
		}

		Obsidian.LOGGER.info(moduleText, OBSIDIAN_ADDONS.keySet().size());

		for (IAddonPack pack : OBSIDIAN_ADDONS) {
			String name;
			String folderName;
			String id;
			String format;
			String version;
			if (pack.getConfigPackInfo() instanceof LegacyObsidianAddonInfo legacyObsidianAddonInfo) {
				name = legacyObsidianAddonInfo.displayName;
				folderName = legacyObsidianAddonInfo.folderName;
				id = legacyObsidianAddonInfo.namespace;
				format = "obsidian";
				if (legacyObsidianAddonInfo.version != null && !legacyObsidianAddonInfo.version.isEmpty()) {
					version = legacyObsidianAddonInfo.version;
				} else {
					version = String.valueOf(legacyObsidianAddonInfo.addonVersion);
				}
			} else {
				ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) pack.getConfigPackInfo();
				name = addonInfo.addon.name;
				folderName = addonInfo.addon.folderName;
				id = addonInfo.addon.id;
				format = addonInfo.addon.format;
				version = addonInfo.addon.version != null ? addonInfo.addon.version : "";
			}

			Obsidian.LOGGER.info(" - {}", name);

			Path packRoot = Path.of(OBSIDIAN_ADDON_DIRECTORY.getPath(), folderName);
			ContentPackSyncManager.registerPack(id, version, format, folderName, packRoot);

			String path = OBSIDIAN_ADDON_DIRECTORY.getPath() + "/" + folderName + "/content/" + id;
			REGISTRY_HELPER = RegistryHelper.createRegistryHelper(id);

			try {
				Registries.ADDON_MODULE_REGISTRY.forEach(addonModule -> loadAddonModule(pack, new BasicAddonInfo(id, path, format), addonModule));
			} catch (Exception throwable) {
				throwable.printStackTrace();
			}

			ScriptManager mgr = new ScriptManager(Path.of(OBSIDIAN_ADDON_DIRECTORY.getPath(), folderName));
			try {
				mgr.loadAll();
				mgr.registerFabricEvents();
				managers.put(id, mgr);
			} catch (IOException _) {
			}

			Path scriptsRoot = packRoot.resolve("scripts");
			if (java.nio.file.Files.isDirectory(scriptsRoot)) {
				var runtime = new io.github.vampirestudios.obsidian.scripting.std.ObsPackRuntime(scriptsRoot);
				try {
					runtime.loadAll();         // parse all *.obs
					runtime.registerEvents();  // hook Fabric events (join)
					runtime.armSchedules();    // start repeating tasks
					runtime.registerCommands();
					OBS_PACKS.put(id, runtime);
					Obsidian.LOGGER.info("Loaded {} .obs scripts for pack '{}'", runtime.scriptCount(), id);
				} catch (Exception ex) {
					Obsidian.LOGGER.error("Failed loading .obs for pack '{}': {}", id, ex.getMessage(), ex);
				}
			} else {
				Obsidian.LOGGER.debug("No .obs scripts folder for pack '{}'", id);
			}
		}

		registerCommands();
//		ContentPackSyncNetworking.initializeServerHandlers();
		// Register all custom commands
		CommandRegistrationCallback.EVENT.register((disp, _, _) -> {
			// for each pack-manager pair
			managers.forEach((_, mgr) -> {
				mgr.commandDefs.forEach((cmdName, def) -> {
					// 1) strip leading slash
					String literal = cmdName.startsWith("/") ? cmdName.substring(1) : cmdName;
					LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(literal);

					// 2) build the argument chain properly
					List<ArgumentBuilder<CommandSourceStack, ?>> nodes = getBuilders(def, root);

					// 3) count leading required (< >) params
					int minArgs = 0;
					for (ParamDef pd : def.params()) {
						if (pd.required()) minArgs++;
						else break;
					}

					// 4) attach executes() to every prefix ≥ minArgs
					for (int count = minArgs; count <= nodes.size(); count++) {
						ArgumentBuilder<CommandSourceStack, ?> target =
								(count == 0 ? root : nodes.get(count - 1));
						int finalCount = count;
						target.executes(ctx -> {
							ServerPlayer player = ctx.getSource().getPlayerOrException();
							ServerLevel world = ctx.getSource().getLevel();
							Map<String, String> eventVars = new HashMap<>();

							// extract exactly `finalCount` arguments by name & type
							for (int i = 0; i < finalCount; i++) {
								ParamDef pd = def.params().get(i);
								String name = pd.name();
								String val = switch (pd.type()) {
									case INTEGER, TIME -> String.valueOf(
											IntegerArgumentType.getInteger(ctx, name));
									case FLOAT -> String.valueOf(
											FloatArgumentType.getFloat(ctx, name));
									case BOOL -> String.valueOf(
											BoolArgumentType.getBool(ctx, name));
									case TEXT, WORD -> StringArgumentType.getString(ctx, name);
									case COLOR -> ColorArgument.getColor(ctx, name).toString();
									case ENTITY -> {
										var e = EntityArgument.getEntity(ctx, name);
										yield e.getUUID().toString();
									}
									case ENTITIES -> {
										var list = EntityArgument.getEntities(ctx, name);
										yield list.stream()
												.map(e2 -> e2.getUUID().toString())
												.collect(Collectors.joining(","));
									}
									case PLAYER -> {
										var p = EntityArgument.getPlayer(ctx, name);
										yield p.getGameProfile().name();
									}
									case PLAYERS -> {
										var pls = EntityArgument.getPlayers(ctx, name);
										yield pls.stream()
												.map(p2 -> p2.getGameProfile().name())
												.collect(Collectors.joining(","));
									}
									case GAME_MODE -> GameModeArgument.getGameMode(ctx, name).getName();
									case BLOCK_POS -> {
										var bp = BlockPosArgument.getLoadedBlockPos(ctx, name);
										yield bp.getX() + "," + bp.getY() + "," + bp.getZ();
									}
									case UUID -> UuidArgument.getUuid(ctx, name).toString();
									case ROTATION -> {
										var rot = RotationArgument.getRotation(ctx, name)
												.getRotation(ctx.getSource());
										yield rot.x + "," + rot.y;
									}
									case ANGLE -> String.valueOf(
											AngleArgument.getAngle(ctx, name));
									case SWIZZLE -> SwizzleArgument.getSwizzle(ctx, name).toString();
									case VEC2 -> {
										var v2 = Vec2Argument.getVec2(ctx, name);
										yield v2.x + "," + v2.y;
									}
									case VEC3 -> {
										var v3 = Vec3Argument.getVec3(ctx, name);
										yield v3.x + "," + v3.y + "," + v3.z;
									}
								};
								eventVars.put(name, val);
							}

							mgr.runCommand(cmdName, player, world, eventVars);
							return 1;
						});
					}

					// 5) register the command tree
					disp.register(root);
				});
			});
		});
	}

	private static @NotNull List<ArgumentBuilder<CommandSourceStack, ?>> getBuilders(ScriptParser.CommandDef def, LiteralArgumentBuilder<CommandSourceStack> root) {
		List<ArgumentBuilder<CommandSourceStack, ?>> nodes = new ArrayList<>();
		ArgumentBuilder<CommandSourceStack, ?> current = root;
		for (ParamDef pd : def.params()) {
			ArgumentBuilder<CommandSourceStack, ?> child = switch (pd.type()) {
				case INTEGER, TIME -> Commands.argument(pd.name(), IntegerArgumentType.integer());
				case FLOAT -> Commands.argument(pd.name(), FloatArgumentType.floatArg());
				case BOOL -> Commands.argument(pd.name(), BoolArgumentType.bool());
				case TEXT -> Commands.argument(pd.name(), StringArgumentType.greedyString());
				case WORD -> Commands.argument(pd.name(), StringArgumentType.word());
				case COLOR -> Commands.argument(pd.name(), ColorArgument.color());
				case ENTITY -> Commands.argument(pd.name(), EntityArgument.entity());
				case ENTITIES -> Commands.argument(pd.name(), EntityArgument.entities());
				case PLAYER -> Commands.argument(pd.name(), EntityArgument.player());
				case PLAYERS -> Commands.argument(pd.name(), EntityArgument.players());
				case GAME_MODE -> Commands.argument(pd.name(), GameModeArgument.gameMode());
				case BLOCK_POS -> Commands.argument(pd.name(), BlockPosArgument.blockPos());
				case UUID -> Commands.argument(pd.name(), UuidArgument.uuid());
				case ROTATION -> Commands.argument(pd.name(), RotationArgument.rotation());
				case ANGLE -> Commands.argument(pd.name(), AngleArgument.angle());
				case SWIZZLE -> Commands.argument(pd.name(), SwizzleArgument.swizzle());
				case VEC2 -> Commands.argument(pd.name(), Vec2Argument.vec2());
				case VEC3 -> Commands.argument(pd.name(), Vec3Argument.vec3());
			};

			current.then(child);
			current = child;
			nodes.add(current);
		}
		return nodes;
	}

	private static void registerCommands() {
		CommandRegistrationCallback.EVENT.register((disp, _, _) -> {
			disp.register(Commands.literal("obsidian")
					.then(Commands.literal("reload")
							// /obsidian reload <pack>
							.then(Commands.argument("pack", StringArgumentType.word())
									.suggests((ctx, builder) -> {
										// manually suggest each key in managers
										managers.keySet().forEach(builder::suggest);
										return builder.buildFuture();
									})
									.executes(ctx -> {
										String pack = StringArgumentType.getString(ctx, "pack");
										ScriptManager mgr = managers.get(pack);
										if (mgr == null) {
											ctx.getSource()
													.sendFailure(Component.literal("§cUnknown pack: " + pack));
											return 0;
										}
										int count;
										try {
											count = mgr.reload();
										} catch (IOException e) {
											ctx.getSource()
													.sendFailure(Component.literal("Reload failed: " + e.getMessage()));
											return 0;
										}
										var rt = OBS_PACKS.get(pack);
										if (rt != null) {
											try {
												int i = rt.reload();
												ctx.getSource()
														.sendSuccess(() -> Component.literal("Loaded " + i + " .obs scripts for pack '" + pack + "'"), false);
											} catch (IOException e) {
												throw new RuntimeException(e);
											}
										}
										ctx.getSource()
												.sendSuccess(() -> Component.literal("§aReloaded " + count + " scripts in " + pack), false);
										return 1;
									})
							)
							.executes(ctx -> {
								int total = managers.values().stream()
										.mapToInt(m -> {
											try {
												return m.reload();
											} catch (IOException e) {
												return 0;
											}
										})
										.sum();
								OBS_PACKS.values().forEach(rt -> {
									try {
										int i = rt.reload();

										ctx.getSource()
												.sendSuccess(() -> Component.literal("Loaded " + i + " .obs scripts across all pack"), false);
									} catch (Exception ignored) {
									}
								});

								ctx.getSource()
										.sendSuccess(() -> Component.literal("§aReloaded " + total + " scripts across all packs"), false);
								return 1;
							})
					)
					.then(Commands.literal("sync")
							.executes(ctx -> {
								boolean dispatched = ContentPackSyncNetworking.broadcastManifest(ctx.getSource().getServer());
								if (!dispatched) {
									ctx.getSource().sendFailure(Component.literal("§cNo Obsidian content packs are loaded to sync."));
									return 0;
								}
								ctx.getSource().sendSuccess(() -> Component.literal("§aSent active Obsidian content packs to connected players."), false);
								return 1;
							}))
			);
		});
	}

	private static void createObsidianAddonsFolder() {
		OBSIDIAN_ADDON_DIRECTORY.mkdirs();
	}

	public static BlockState getState(net.minecraft.world.level.block.Block block, Map<String, String> jsonProperties) {
		BlockState blockstate = block.defaultBlockState();
		Collection<Property<?>> properties = blockstate.getProperties();
		for (Property property : properties) {
			String propertyName = property.getName();
			if (jsonProperties.containsKey(propertyName)) {
				String valueName = jsonProperties.get(propertyName);
				Optional valueOpt = property.getValue(valueName);
				if (valueOpt.isPresent()) {
					Comparable value = (Comparable) valueOpt.get();
					blockstate = blockstate.setValue(property, value);
				} else {
					Obsidian.LOGGER.error("Property[{}={}] doesn't exist for {}", propertyName, valueName, block);
				}
				jsonProperties.remove(propertyName);
			}
		}
		if (!jsonProperties.isEmpty()) {
			Joiner joiner = Joiner.on(", ");
			Obsidian.LOGGER.error("The following properties do not exist in {}: {}", block, joiner.join(jsonProperties.keySet()));
		}
		return blockstate;
	}

	private static void loadAddonModule(IAddonPack addon, BasicAddonInfo id, AddonModule addonModule) {
		if (Paths.get(id.addonPath(), addonModule.getType()).toFile().exists()) {
			for (File file : Objects.requireNonNull(Paths.get(id.addonPath(), addonModule.getType()).toFile().listFiles())) {
				if (file.isFile()) {
					try {
						addonModule.init(addon, file, id);
					} catch (SyntaxError | IOException | DeserializationException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static <T> T register(Registry<T> list, String type, Identifier name, T idk) {
		if (type != null && !type.isEmpty())
			Obsidian.LOGGER.info("Registered {} {}.", type, name);
		if (list.get(name).isPresent()) return list.getValue(name);
		else return Registry.register(list, name, idk);
	}

	public static void failedRegistering(String type, String name, Exception e) {
		failedRegistering(type, Identifier.tryParse(name), e);
	}

	public static void failedRegistering(String type, Identifier name, Exception e) {
		Obsidian.LOGGER.error("Failed to register {} {}.", type, name);
		Obsidian.LOGGER.error(e.getMessage(), e);
	}

	public net.minecraft.world.level.block.Block register(Identifier name, net.minecraft.world.level.block.Block block, ResourceKey<net.minecraft.world.item.CreativeModeTab> tab) {
		Block block1 = register(name, block, new net.minecraft.world.item.Item.Properties());
		CreativeModeTabEvents.modifyOutputEvent(tab).register(entries -> entries.accept(block1));
		return block1;
	}

	public net.minecraft.world.level.block.Block register(Identifier name, net.minecraft.world.level.block.Block block, net.minecraft.world.item.Item.Properties properties) {
		return register(name, block, new BlockItem(block, properties));
	}

	public net.minecraft.world.level.block.Block register(Identifier name, net.minecraft.world.level.block.Block block, BlockItem item) {
		Registry.register(net.minecraft.core.registries.BuiltInRegistries.BLOCK, name, block);
		Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM, name, item);
		return block;
	}

}
