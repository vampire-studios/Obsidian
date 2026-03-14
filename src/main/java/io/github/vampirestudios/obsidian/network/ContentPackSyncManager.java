package io.github.vampirestudios.obsidian.network;

import io.github.vampirestudios.obsidian.Obsidian;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ContentPackSyncManager {
        private static final Map<String, RegisteredPack> PACKS = new LinkedHashMap<>();

        private ContentPackSyncManager() {
        }

        public static void reset() {
                PACKS.clear();
        }

        public static void registerPack(String id, String version, String format, String folderName, Path root) {
                PACKS.put(id, new RegisteredPack(id, version, format, folderName, root));
        }

        public static Collection<RegisteredPack> getPacks() {
                return Collections.unmodifiableCollection(PACKS.values());
        }

        public static boolean isEmpty() {
                return PACKS.isEmpty();
        }

        public static byte[] createBundle(RegisteredPack pack) {
                try {
                        return zipDirectory(pack.root());
                } catch (IOException e) {
                        Obsidian.LOGGER.error("Failed to bundle content pack '{}' for sync", pack.id(), e);
                        return new byte[0];
                }
        }

        public static String sha256Hex(byte[] data) {
                try {
                        byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
                        return HexFormat.of().formatHex(hash);
                } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                }
        }

        private static byte[] zipDirectory(Path root) throws IOException {
                if (!Files.exists(root)) {
                        return new byte[0];
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try (ZipOutputStream zipOut = new ZipOutputStream(outputStream);
                     var stream = Files.walk(root)) {
                        stream.filter(Files::isRegularFile)
                                .forEach(path -> {
                                        Path relative = root.relativize(path);
                                        String entryName = relative.toString().replace('\\', '/');
                                        try {
                                                ZipEntry entry = new ZipEntry(entryName);
                                                zipOut.putNextEntry(entry);
                                                Files.copy(path, zipOut);
                                                zipOut.closeEntry();
                                        } catch (IOException e) {
                                                Obsidian.LOGGER.error("Failed adding '{}' to synced bundle", entryName, e);
                                        }
                                });
                }

                return outputStream.toByteArray();
        }

        public record RegisteredPack(String id, String version, String format, String folderName, Path root) {
        }
}