package io.github.vampirestudios.obsidian.network;

import io.github.vampirestudios.obsidian.Const;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record ContentPackManifestPayload(int schema, List<PackEntry> packs) implements CustomPacketPayload {
        public static final Type<ContentPackManifestPayload> TYPE = new Type<>(Const.id("content_pack_manifest"));
        public static final StreamCodec<FriendlyByteBuf, ContentPackManifestPayload> CODEC = StreamCodec.of(
                        (buf, payload) -> payload.write(buf), ContentPackManifestPayload::read);

        public ContentPackManifestPayload(int schema, List<PackEntry> packs) {
                this.schema = schema;
                this.packs = List.copyOf(packs);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
                return TYPE;
        }

        private void write(FriendlyByteBuf buf) {
                buf.writeVarInt(schema);
                buf.writeVarInt(packs.size());
                packs.forEach(pack -> {
                        buf.writeUtf(pack.id());
                        buf.writeUtf(pack.version());
                        buf.writeUtf(pack.format());
                        buf.writeUtf(pack.folderName());
                        buf.writeByteArray(pack.bundle());
                });
        }

        private static ContentPackManifestPayload read(FriendlyByteBuf buf) {
                int schema = buf.readVarInt();
                int count = buf.readVarInt();
                List<PackEntry> packs = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                        String id = buf.readUtf();
                        String version = buf.readUtf();
                        String format = buf.readUtf();
                        String folderName = buf.readUtf();
                        byte[] bundle = buf.readByteArray();
                        packs.add(new PackEntry(id, version, format, folderName, bundle));
                }
                return new ContentPackManifestPayload(schema, packs);
        }

        public record PackEntry(String id, String version, String format, String folderName, byte[] bundle) {
        }
}