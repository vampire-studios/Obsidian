package io.github.vampirestudios.obsidian.api.dataexchange.handler;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.dataexchange.DataHandler;
import io.github.vampirestudios.obsidian.api.dataexchange.DataHandlerDescriptor;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import ru.bclib.BCLib;
import ru.bclib.api.dataexchange.handler.DataExchange.AutoSyncID;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class RequestFiles extends DataHandler {
	public static DataHandlerDescriptor DESCRIPTOR = new DataHandlerDescriptor(new Identifier(Obsidian.MOD_ID, "request_files"), RequestFiles::new, false, false);
	static String currentToken = "";
	
	protected List<AutoSyncID> files;
	private RequestFiles(){
		this(null);
	}
	
	public RequestFiles(List<AutoSyncID> files) {
		super(DESCRIPTOR.IDENTIFIER, false);
		this.files = files;
	}
	
	@Override
	protected void serializeData(PacketByteBuf buf) {
		newToken();
		writeString(buf, currentToken);

		buf.writeInt(files.size());
		
		for (AutoSyncID a : files){
			writeString(buf, a.modID);
			writeString(buf, a.uniqueID);
		}
	}

	String receivedToken = "";
	@Override
	protected void deserializeFromIncomingData(PacketByteBuf buf, PacketSender responseSender, boolean fromClient) {
		receivedToken = readString(buf);
		int size = buf.readInt();
		files = new ArrayList<>(size);
		
		Obsidian.LOGGER.info("Client requested " + size + " Files:");
		for (int i=0; i<size; i++){
			String modID = readString(buf);
			String uID = readString(buf);
			AutoSyncID asid = new AutoSyncID(modID, uID);
			files.add(asid);
			Obsidian.LOGGER.info("    - " + asid);
		}
	}
	
	@Override
	protected void runOnGameThread(MinecraftClient client, MinecraftServer server, boolean isClient) {
		List<AutoFileSyncEntry> syncEntries = files
				.stream().map(asid -> AutoFileSyncEntry.findMatching(asid))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		reply(new SendFiles(syncEntries, receivedToken), server);
	}

	public static void newToken(){
		currentToken =  UUID.randomUUID().toString();
	}

	static {
		newToken();
	}
}