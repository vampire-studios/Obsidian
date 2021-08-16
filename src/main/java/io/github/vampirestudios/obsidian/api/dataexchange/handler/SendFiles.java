package io.github.vampirestudios.obsidian.api.dataexchange.handler;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.dataexchange.DataHandler;
import io.github.vampirestudios.obsidian.api.dataexchange.DataHandlerDescriptor;
import io.github.vampirestudios.obsidian.gui.screens.ConfirmRestartScreen;
import io.github.vampirestudios.obsidian.utils.Pair;
import io.github.vampirestudios.obsidian.utils.Triple;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SendFiles extends DataHandler {
	public static DataHandlerDescriptor DESCRIPTOR = new DataHandlerDescriptor(new Identifier(Obsidian.MOD_ID, "send_files"), SendFiles::new, false, false);

	protected List<AutoFileSyncEntry> files;
	private String token = "";
	public SendFiles(){
		this(null, "");
	}
	public SendFiles(List<AutoFileSyncEntry> files, String token) {
		super(DESCRIPTOR.IDENTIFIER, true);
		this.files = files;
		this.token = token;
	}

	public static boolean acceptFiles() {
		return /*Configs.CLIENT_CONFIG.getBoolean("client_sync", "acceptFiles", true)*/true;
	}
	
	@Override
	protected void serializeData(PacketByteBuf buf) {
		List<AutoFileSyncEntry> existingFiles = files.stream().filter(e -> e.fileName.exists()).collect(Collectors.toList());
		/*
		//this will try to send a file that was not registered or requested by the client
		existingFiles.add(new AutoFileSyncEntry("none", new File("D:\\MinecraftPlugins\\BetterNether\\run\\server.properties"),true,(a, b, content) -> {
			System.out.println("Got Content:" + content.length);
			return true;
		}));*/
		writeString(buf, token);
		buf.writeInt(existingFiles.size());

		Obsidian.LOGGER.info("Sending " + existingFiles.size() + " Files to Client:");
		for (AutoFileSyncEntry entry : existingFiles) {
			int length = entry.serializeContent(buf);
			Obsidian.LOGGER.info("    - " + entry + " (" + length + " Bytes)");
		}
	}

	private List<Pair<AutoFileSyncEntry, byte[]>> receivedFiles;
	@Override
	protected void deserializeFromIncomingData(PacketByteBuf buf, PacketSender responseSender, boolean fromClient) {
		if (acceptFiles()) {
			token = readString(buf);
			if (!token.equals(RequestFiles.currentToken)) {
				RequestFiles.newToken();
				Obsidian.LOGGER.error("Unrequested File Transfer!");
				receivedFiles = new ArrayList<>(0);
				return;
			}
			RequestFiles.newToken();

			int size = buf.readInt();
			receivedFiles = new ArrayList<>(size);
			Obsidian.LOGGER.info("Server sent " + size + " Files:");
			for (int i = 0; i < size; i++) {
				Triple<AutoFileSyncEntry, byte[], DataExchange.AutoSyncID> p = AutoFileSyncEntry.deserializeContent(buf);
				if (p.first != null) {
					receivedFiles.add(p);
					Obsidian.LOGGER.info("    - " + p.first + " (" + p.second.length + " Bytes)");
				} else {
					Obsidian.LOGGER.error("   - Failed to receive File " + p.third + ", possibly sent from a Mod that is not installed on the client.");
				}
			}
		}
	}
	
	@Override
	protected void runOnGameThread(MinecraftClient client, MinecraftServer server, boolean isClient) {
		if (acceptFiles()) {
			Obsidian.LOGGER.info("Writing Files:");
			for (Pair<AutoFileSyncEntry, byte[]> entry : receivedFiles) {
				final AutoFileSyncEntry e = entry.first;
				final byte[] data = entry.second;
				Path path = e.fileName.toPath();
				Obsidian.LOGGER.info("    - Writing " + path + " (" + data.length + " Bytes)");
				try {
					Files.write(path, data);
				} catch (IOException ioException) {
					Obsidian.LOGGER.error("    --> Writing " + e.fileName + " failed: " + ioException);
				}
			}

			showConfirmRestart(client);
		}
	}

	@Environment(EnvType.CLIENT)
	protected void showConfirmRestart(MinecraftClient client){
		client.setScreen(new ConfirmRestartScreen(() -> {
			MinecraftClient.getInstance().setScreen(null);
			client.stop();
		}));

	}
}