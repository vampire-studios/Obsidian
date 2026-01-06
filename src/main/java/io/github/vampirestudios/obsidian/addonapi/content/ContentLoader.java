package io.github.vampirestudios.obsidian.addonapi.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.addonapi.command.CommandDefinition;
import io.github.vampirestudios.obsidian.addonapi.menu.MenuDefinition;
import io.github.vampirestudios.obsidian.addonapi.menu.ScreenMenuDefinition;
import io.github.vampirestudios.obsidian.addonapi.registry.AddonCommandRegistry;
import io.github.vampirestudios.obsidian.addonapi.registry.AddonMenuRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;

public class ContentLoader {

    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final Logger LOGGER = Obsidian.LOGGER;

    public static void reload(MinecraftServer server) {
        AddonMenuRegistry.clear();
        AddonCommandRegistry.clear();

        Path root = server.getServerDirectory().resolve("content");
        if (!Files.isDirectory(root)) {
            LOGGER.info("[AddonAPI] No content directory at {}", root);
            return;
        }

        try (Stream<Path> addons = Files.list(root)) {
            addons.filter(Files::isDirectory).forEach(ContentLoader::loadAddon);
        } catch (IOException e) {
            LOGGER.error("[AddonAPI] Failed to list content directory", e);
        }

        LOGGER.info("[AddonAPI] Loaded {} menus and {} commands from content/",
                AddonMenuRegistry.all().size(), AddonCommandRegistry.all().size());
    }

    private static void loadAddon(Path addonDir) {
        String addonId = addonDir.getFileName().toString().toLowerCase(Locale.ROOT);
        Path menusDir = addonDir.resolve("menus");
        Path commandsDir = addonDir.resolve("commands");

        if (Files.isDirectory(menusDir)) {
            loadMenus(addonId, menusDir);
        }
        if (Files.isDirectory(commandsDir)) {
            loadCommands(addonId, commandsDir);
        }
    }

    private static void loadMenus(String addonId, Path menusDir) {
        try (Stream<Path> files = Files.walk(menusDir)) {
            files.filter(p -> p.toString().endsWith(".json"))
                    .forEach(path -> {
                        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                            JsonObject root = GSON.fromJson(reader, JsonObject.class);

                            String type = root.has("type") ? root.get("type").getAsString() : "screen";

                            String idStr;
                            if (root.has("id")) {
                                idStr = root.get("id").getAsString();
                            } else {
                                idStr = addonId + ":" + stripJson(path.getFileName().toString());
                            }
                            Identifier id = Identifier.parse(idStr);

                            MenuDefinition def;
                            if ("screen".equals(type)) {
                                def = GSON.fromJson(root, ScreenMenuDefinition.class);
                            } else {
                                def = GSON.fromJson(root, MenuDefinition.class);
                            }

                            def.id = id.toString();
                            def.type = type;

                            AddonMenuRegistry.put(id, def);
                        } catch (Exception e) {
                            LOGGER.error("[AddonAPI] Failed to load menu from {}", path, e);
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("[AddonAPI] Failed to scan menus in {}", menusDir, e);
        }
    }

    private static void loadCommands(String addonId, Path commandsDir) {
        try (Stream<Path> files = Files.walk(commandsDir)) {
            files.filter(p -> p.toString().endsWith(".json"))
                    .forEach(path -> {
                        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                            CommandDefinition def = GSON.fromJson(reader, CommandDefinition.class);
                            if (def.id == null || def.id.isEmpty()) {
                                def.id = addonId + ":" + stripJson(path.getFileName().toString());
                            }
							Identifier id = Identifier.parse(def.id);
                            AddonCommandRegistry.put(id, def);
                        } catch (Exception e) {
                            LOGGER.error("[AddonAPI] Failed to load command from {}", path, e);
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("[AddonAPI] Failed to scan commands in {}", commandsDir, e);
        }
    }

    private static String stripJson(String filename) {
        if (filename.endsWith(".json")) {
            return filename.substring(0, filename.length() - 5);
        }
        return filename;
    }
}
