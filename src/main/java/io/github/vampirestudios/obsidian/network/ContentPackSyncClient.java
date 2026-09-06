package io.github.vampirestudios.obsidian.network;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
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

	public static void registerConfigReceivers() {
		ClientConfigurationNetworking.registerGlobalReceiver(ContentPackManifestPayload.TYPE,
				// Runs on the netty thread during the configuration phase, BEFORE
				// Fabric's registry-sync task executes.  We extract the files AND
				// immediately call loadServerObsidianAddons() so the registry entries
				// exist by the time Fabric checks them.  The duplicate-guard in
				// ObsidianAddonLoader.register() and MappedRegistryMixin make
				// repeated calls safe.
				(payload, context) -> extractAndRegisterPacks(payload));
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

	// Called during the configuration phase: extract new packs to disk, then
	// immediately register their content so the registry entries exist before
	// Fabric's registry-sync task runs its mismatch check.
	private static void extractAndRegisterPacks(ContentPackManifestPayload payload) {
		Obsidian.LOGGER.info("[Obsidian Sync] Config-phase manifest received. {} pack(s).", payload.packs().size());
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
			Path target = serverAddonsDir.resolve(pack.sha256());
			if (Files.isDirectory(target)) {
				Obsidian.LOGGER.debug("Server pack '{}' already cached ({}), skipping", pack.id(), pack.sha256().substring(0, 8));
				continue;
			}
			try {
				unzip(pack.bundle(), target);
				anyNewPack = true;
				Obsidian.LOGGER.info("Extracted server pack '{}' ({})", pack.id(), pack.sha256().substring(0, 8));
			} catch (IOException e) {
				Obsidian.LOGGER.error("Failed writing synced content pack '{}'", pack.id(), e);
			}
		}

		// Always (re)run loadServerObsidianAddons so any pack that was on disk but
		// not yet in the OBSIDIAN_ADDONS registry gets registered now.  The
		// duplicate-guard in ObsidianAddonLoader.register() skips already-known
		// entries, and MappedRegistryMixin keeps the Minecraft registry writable.
		Obsidian.LOGGER.info("Registering server obsidian addons into client registry (config phase)");
		ObsidianAddonLoader.loadServerObsidianAddons();
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