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

                Path serverAddonsDir = FabricLoader.getInstance().getGameDir().resolve("server_obsidian_addons");
                boolean anyNewPack = false;
                for (var pack : payload.packs()) {
                        if (pack.bundle().length == 0) {
                                continue;
                        }
                        // Use the SHA-256 hash as the folder name so the content is not
                        // immediately browsable and we can skip re-extracting unchanged packs.
                        Path target = serverAddonsDir.resolve(pack.sha256());
                        if (Files.isDirectory(target)) {
                                Obsidian.LOGGER.debug("Server pack '{}' already cached ({}), skipping", pack.id(), pack.sha256().substring(0, 8));
                                continue;
                        }
                        try {
                                unzip(pack.bundle(), target);
                                anyNewPack = true;
                                Obsidian.LOGGER.info("Cached server pack '{}' as {}", pack.id(), pack.sha256().substring(0, 8));
                        } catch (IOException e) {
                                Obsidian.LOGGER.error("Failed writing synced content pack '{}'", pack.id(), e);
                        }
                }

                if (anyNewPack) {
                        try {
                                client.reloadResourcePacks().join();
                        } catch (Exception e) {
                                Obsidian.LOGGER.error("Failed to reload resources after syncing content packs", e);
                        }
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