package io.github.vampirestudios.obsidian.network;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public final class ContentPackSyncNetworking {
        private static boolean serverRegistered;
        private static boolean clientRegistered;
        private static boolean playPayloadRegistered;
        private static boolean configPayloadRegistered;

        private ContentPackSyncNetworking() {
        }

        public static void initializeServerHandlers() {
                registerPlayPayloadType();
                registerConfigPayloadType();
                if (serverRegistered) {
                        return;
                }
                serverRegistered = true;
                Obsidian.LOGGER.info("[Obsidian Sync] Server handlers registered.");

                ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
                        Obsidian.LOGGER.info("[Obsidian Sync] CONFIGURE fired. packs empty={}", ContentPackSyncManager.isEmpty());
                        if (ContentPackSyncManager.isEmpty()) {
                                return;
                        }
                        boolean canSend = ServerConfigurationNetworking.canSend(handler, ContentPackManifestPayload.TYPE);
                        Obsidian.LOGGER.info("[Obsidian Sync] canSend={}", canSend);
                        if (!canSend) {
                                Obsidian.LOGGER.warn("[Obsidian Sync] Client does not support config-phase channel — skipping config send.");
                                return;
                        }
                        sendConfigManifest(handler);
                });

                ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                        Obsidian.LOGGER.info("[Obsidian Sync] JOIN fired. packs empty={}", ContentPackSyncManager.isEmpty());
                        if (ContentPackSyncManager.isEmpty()) {
                                return;
                        }
                        sendManifest(handler.player);
                });
        }

        public static void registerClientReceivers() {
                registerPlayPayloadType();
                registerConfigPayloadType();
                if (clientRegistered) {
                        return;
                }
                if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
                        return;
                }
                clientRegistered = true;
                ContentPackSyncClient.registerReceivers();
                ContentPackSyncClient.registerConfigReceivers();
        }

        public static boolean broadcastManifest(MinecraftServer server) {
                if (ContentPackSyncManager.isEmpty()) {
                        return false;
                }
                server.getPlayerList().getPlayers().forEach(ContentPackSyncNetworking::sendManifest);
                return true;
        }

        public static void sendManifest(ServerPlayer player) {
                var packs = ContentPackSyncManager.getPacks();
                if (packs.isEmpty()) {
                        return;
                }

                var entries = buildEntries();
                if (entries.isEmpty()) {
                        return;
                }

                ServerPlayNetworking.send(player,
                        new ContentPackManifestPayload(ObsidianAddonLoader.SCHEMA_VERSION, entries));
        }

        private static void sendConfigManifest(ServerConfigurationPacketListenerImpl handler) {
                var entries = buildEntries();
                if (entries.isEmpty()) {
                        return;
                }

                ServerConfigurationNetworking.send(handler,
                        new ContentPackManifestPayload(ObsidianAddonLoader.SCHEMA_VERSION, entries));
        }

        private static java.util.List<ContentPackManifestPayload.PackEntry> buildEntries() {
                return ContentPackSyncManager.getPacks().stream()
                        .map(pack -> {
                                byte[] bundle = ContentPackSyncManager.createBundle(pack);
                                String sha256 = ContentPackSyncManager.sha256Hex(bundle);
                                return new ContentPackManifestPayload.PackEntry(pack.id(), pack.version(), pack.format(),
                                        pack.folderName(), sha256, bundle);
                        })
                        .toList();
        }

        private static void registerPlayPayloadType() {
                if (playPayloadRegistered) {
                        return;
                }
                playPayloadRegistered = true;
                PayloadTypeRegistry.clientboundPlay().register(ContentPackManifestPayload.TYPE,
                        ContentPackManifestPayload.CODEC);
        }

        private static void registerConfigPayloadType() {
                if (configPayloadRegistered) {
                        return;
                }
                configPayloadRegistered = true;
                PayloadTypeRegistry.clientboundConfiguration().register(ContentPackManifestPayload.TYPE,
                        ContentPackManifestPayload.CODEC);
        }
}