package io.github.vampirestudios.obsidian.network;

import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ContentPackSyncNetworking {
        private static boolean serverRegistered;
        private static boolean clientRegistered;
        private static boolean payloadRegistered;

        private ContentPackSyncNetworking() {
        }

        public static void initializeServerHandlers() {
                registerPayloadType();
                if (serverRegistered) {
                        return;
                }
                serverRegistered = true;
                ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                        if (ContentPackSyncManager.isEmpty()) {
                                return;
                        }
                        sendManifest(handler.player);
                });
        }

        public static void registerClientReceivers() {
                registerPayloadType();
                if (clientRegistered) {
                        return;
                }
                if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
                        return;
                }
                clientRegistered = true;
                ContentPackSyncClient.registerReceivers();
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

                var entries = packs.stream()
                        .map(pack -> new ContentPackManifestPayload.PackEntry(pack.id(), pack.version(), pack.format(),
                                pack.folderName(), ContentPackSyncManager.createBundle(pack)))
                        .toList();

                if (entries.isEmpty()) {
                        return;
                }

                ServerPlayNetworking.send(player,
                        new ContentPackManifestPayload(ObsidianAddonLoader.SCHEMA_VERSION, entries));
        }

        private static void registerPayloadType() {
                if (payloadRegistered) {
                        return;
                }
                PayloadTypeRegistry.playS2C().register(ContentPackManifestPayload.TYPE,
                        ContentPackManifestPayload.CODEC);
                payloadRegistered = true;
        }
}