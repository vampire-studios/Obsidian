package io.github.vampirestudios.obsidian.api.dataexchange.handler;

import io.github.vampirestudios.obsidian.api.dataexchange.DataHandler;
import io.github.vampirestudios.obsidian.api.dataexchange.FileHash;
import io.github.vampirestudios.obsidian.utils.Pair;
import io.github.vampirestudios.obsidian.utils.Triple;
import net.minecraft.network.PacketByteBuf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class AutoFileSyncEntry extends DataExchange.AutoSyncID {
    public final DataExchange.NeedTransferPredicate needTransfer;
    public final File fileName;
    public final boolean requestContent;
    private FileHash hash;

    AutoFileSyncEntry(String modID, File fileName, boolean requestContent, DataExchange.NeedTransferPredicate needTransfer) {
        this(modID, fileName.getName(), fileName, requestContent, needTransfer);
    }

    AutoFileSyncEntry(String modID, String uniqueID, File fileName, boolean requestContent, DataExchange.NeedTransferPredicate needTransfer) {
        super(modID, uniqueID);
        this.needTransfer = needTransfer;
        this.fileName = fileName;
        this.requestContent = requestContent;
    }

    public FileHash getFileHash() {
        if (hash == null)
        {
            hash = FileHash.create(modID, fileName, uniqueID);
        }
        return hash;
    }

    public byte[] getContent() {
        if (!fileName.exists()) return new byte[0];
        final Path path = fileName.toPath();

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {

        }
        return new byte[0];
    }

    public int serializeContent(PacketByteBuf buf) {
        DataHandler.writeString(buf, modID);
        DataHandler.writeString(buf, uniqueID);
        return serializeFileContent(buf);
    }

    public static Triple<AutoFileSyncEntry, byte[], DataExchange.AutoSyncID> deserializeContent(PacketByteBuf buf) {
        final String modID = DataHandler.readString(buf);
        final String uniqueID = DataHandler.readString(buf);
        byte[] data = deserializeFileContent(buf);

        AutoFileSyncEntry entry = AutoFileSyncEntry.findMatching(modID, uniqueID);
        return new Triple<>(entry, data, new DataExchange.AutoSyncID(modID, uniqueID));
    }


    public void serialize(PacketByteBuf buf) {
        getFileHash().serialize(buf);
        buf.writeBoolean(requestContent);

        if (requestContent) {
            serializeFileContent(buf);
        }
    }

    public static DataExchange.AutoSyncTriple deserializeAndMatch(PacketByteBuf buf) {
        Pair<FileHash, byte[]> e = deserialize(buf);
        AutoFileSyncEntry match = findMatching(e.first);
        return new DataExchange.AutoSyncTriple(e.first, e.second, match);
    }

    public static Pair<FileHash, byte[]> deserialize(PacketByteBuf buf) {
        FileHash hash = FileHash.deserialize(buf);
        boolean withContent = buf.readBoolean();
        byte[] data = null;
        if (withContent) {
            data = deserializeFileContent(buf);
        }

        return new Pair(hash, data);
    }

    private int serializeFileContent(PacketByteBuf buf) {
        byte[] content = getContent();
        buf.writeInt(content.length);
        buf.writeByteArray(content);
        return content.length;
    }

    private static byte[] deserializeFileContent(PacketByteBuf buf) {
        byte[] data;
        int size = buf.readInt();
        data = buf.readByteArray(size);
        return data;
    }

    public static AutoFileSyncEntry findMatching(FileHash hash) {
        return findMatching(hash.modID, hash.uniqueID);
    }

    public static AutoFileSyncEntry findMatching(DataExchange.AutoSyncID aid) {
        return findMatching(aid.modID, aid.uniqueID);
    }

    public static AutoFileSyncEntry findMatching(String modID, String uniqueID) {
        return DataExchange
                .getInstance()
                .autoSyncFiles
                .stream()
                .filter(asf -> asf.modID.equals(modID) && asf.uniqueID.equals(uniqueID))
                .findFirst()
                .orElse(null);
    }
}