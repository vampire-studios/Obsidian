package io.github.vampirestudios.obsidian.network;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Environment(EnvType.CLIENT)
public final class ContentPackSyncClient {
        private ContentPackSyncClient() {
        }

        public static void registerReceivers() {
                ClientPlayNetworking.registerGlobalReceiver(ContentPackManifestPayload.TYPE,
                        (payload, context) -> context.client().execute(
                                () -> applyPayload(context.client(), payload)));
        }

        private static void applyPayload(Minecraft client, ContentPackManifestPayload payload) {
                if (payload.schema() != ObsidianAddonLoader.SCHEMA_VERSION) {
                        Obsidian.LOGGER.warn(
                                "Server is using Obsidian schema {} but client expects {} - content packs may not load correctly",
                                payload.schema(), ObsidianAddonLoader.SCHEMA_VERSION);
                }

                Path addonsDir = FabricLoader.getInstance().getGameDir().resolve("obsidian_addons");
                payload.packs().forEach(pack -> {
                        if (pack.bundle().length == 0) {
                                return;
                        }
                        Path target = addonsDir.resolve(pack.folderName());
                        try {
                                if (Files.exists(target)) {
                                        try (var walk = Files.walk(target)) {
                                                walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                                                        try {
                                                                Files.deleteIfExists(path);
                                                        } catch (IOException e) {
                                                                Obsidian.LOGGER.error("Failed deleting '{}' during pack sync", path, e);
                                                        }
                                                });
                                        }
                                }
                                unzip(pack.bundle(), target);
                        } catch (IOException e) {
                                Obsidian.LOGGER.error("Failed writing synced content pack '{}'", pack.id(), e);
                        }
                });

                try {
                        client.reloadResourcePacks().join();
                } catch (Exception e) {
                        Obsidian.LOGGER.error("Failed to reload resources after syncing content packs", e);
                }
        }

        private static void unzip(byte[] bundle, Path target) throws IOException {
                Files.createDirectories(target);
                try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bundle))) {
                        ZipEntry entry;
                        while ((entry = zis.getNextEntry()) != null) {
                                if (entry.getName() == null || entry.getName().isBlank()) {
                                        continue;
                                }
                                Path resolved = target.resolve(entry.getName()).normalize();
                                if (!resolved.startsWith(target)) {
                                        Obsidian.LOGGER.warn("Skipping suspicious entry '{}' while syncing pack", entry.getName());
                                        continue;
                                }
                                if (entry.isDirectory()) {
                                        Files.createDirectories(resolved);
                                } else {
                                        Files.createDirectories(resolved.getParent());
                                        if (!resolved.toString().contains(".git")) {
                                                Files.copy(zis, resolved, StandardCopyOption.REPLACE_EXISTING);
                                        }
                                }
                        }
                }
        }

}