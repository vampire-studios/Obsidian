package io.github.vampirestudios.obsidian.api.dataexchange.handler;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.dataexchange.DataExchangeAPI;
import io.github.vampirestudios.obsidian.api.dataexchange.DataHandler;
import io.github.vampirestudios.obsidian.api.dataexchange.DataHandlerDescriptor;
import io.github.vampirestudios.obsidian.utils.ModVersionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HelloClient extends DataHandler {
	public static DataHandlerDescriptor DESCRIPTOR = new DataHandlerDescriptor(new Identifier(Obsidian.MOD_ID, "hello_client"), HelloClient::new, false, false);

	public HelloClient() {
		super(DESCRIPTOR.IDENTIFIER, true);
	}

	public static String getModVersion(String modID){
		Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(modID);
		if (optional.isPresent()) {
			ModContainer modContainer = optional.get();
			return modContainer.getMetadata().getVersion().toString();
		}
		return "0.0.0";
	}

	static String getBCLibVersion(){
		return getModVersion(Obsidian.MOD_ID);
	}

	@Override
	protected void serializeData(PacketByteBuf buf) {
		final String vbclib = getBCLibVersion();
		Obsidian.LOGGER.info("Sending Hello to Client. (server="+vbclib+")");
		final List<String> mods = DataExchangeAPI.registeredMods();

		//write BCLibVersion (=protocol version)
		buf.writeInt(DataFixerAPI.getModVersion(vbclib));

		if (Configs.MAIN_CONFIG.getBoolean(Configs.MAIN_SYNC_CATEGORY, "offerMods", true)) {
			//write Plugin Versions
			buf.writeInt(mods.size());
			for (String modID : mods) {
				writeString(buf, modID);
				final String ver = getModVersion(modID);
				buf.writeInt(DataFixerAPI.getModVersion(ver));
				BCLib.LOGGER.info("    - Listing Mod " + modID + " v" + ver);
			}
		} else {
			Obsidian.LOGGER.info("Server will not list Mods.");
			buf.writeInt(0);
		}

		if (Configs.MAIN_CONFIG.getBoolean(Configs.MAIN_SYNC_CATEGORY, "offerConfigs", true)) {
			//do only include files that exist on the server
			final List<AutoFileSyncEntry> existingAutoSyncFiles = DataExchange
					.getInstance()
					.autoSyncFiles
					.stream()
					.filter(e -> e.fileName.exists())
					.collect(Collectors.toList());

			//send config Data
			buf.writeInt(existingAutoSyncFiles.size());
			for (AutoFileSyncEntry entry : existingAutoSyncFiles) {
				entry.serialize(buf);
				BCLib.LOGGER.info("    - Offering File " + entry);
			}
		} else {
			Obsidian.LOGGER.info("Server will not offer Configs.");
			buf.writeInt(0);
		}
	}

	String bclibVersion ="0.0.0";
	Map<String, String> modVersion = new HashMap<>();
	List<DataExchange.AutoSyncTriple> autoSyncedFiles = null;
	@Override
	protected void deserializeFromIncomingData(PacketByteBuf buf, PacketSender responseSender, boolean fromClient) {
		//read BCLibVersion (=protocol version)
		bclibVersion = ModVersionUtils.getModVersion(buf.readInt());

		//read Plugin Versions
		modVersion = new HashMap<>();
		int count = buf.readInt();
		for (int i=0; i< count; i++) {
			String id = readString(buf);
			String version = DataFixerAPI.getModVersion(buf.readInt());
			modVersion.put(id, version);
		}

		//read config Data
		count = buf.readInt();
		autoSyncedFiles = new ArrayList<>(count);
		for (int i=0; i< count; i++) {
			//System.out.println("Deserializing ");
			DataExchange.AutoSyncTriple t = AutoFileSyncEntry.deserializeAndMatch(buf);
			autoSyncedFiles.add(t);
			//System.out.println(t.first);
		}
	}

	@Override
	protected void runOnGameThread(MinecraftClient client, MinecraftServer server, boolean isClient) {
		String localBclibVersion = getBCLibVersion();
		Obsidian.LOGGER.info("Received Hello from Server. (client="+localBclibVersion+", server="+bclibVersion+")");

		// if (DataFixerAPI.getModVersion(localBclibVersion) != DataFixerAPI.getModVersion(bclibVersion)){
		// 	showBCLibError(client);
		// 	return;
		// }

		List<AutoSyncID> filesToRequest = new ArrayList<>(4);

		for (Map.Entry<String, String> e : modVersion.entrySet()){
			String ver = getModVersion(e.getKey());
			BCLib.LOGGER.info("    - " + e.getKey() + " (client="+ver+", server="+ver+")");
		}

		if (autoSyncedFiles.size()>0) {
			BCLib.LOGGER.info("Files offered by Server:");
		}
		final String requestText = SendFiles.acceptFiles()?"requesting":"differs";
		for (DataExchange.AutoSyncTriple e : autoSyncedFiles) {
			boolean willRequest = false;
			if (e.third == null) {
				willRequest = true;
				filesToRequest.add(new AutoSyncID(e.first.modID, e.first.uniqueID));
			} else if (e.third.needTransfer.test(e.third.getFileHash(), e.first, e.second)) {
				willRequest = true;
				filesToRequest.add(new AutoSyncID(e.first.modID, e.first.uniqueID));
			}

			Obsidian.LOGGER.info("    - " + e + ": " + (willRequest ? (" ("+requestText+")" ):""));
		}

		/*if (filesToRequest.size()>0 && SendFiles.acceptFiles()) {
			showDonwloadConfigs(client, filesToRequest);
			return;
		}*/
	}

	@Environment(EnvType.CLIENT)
	protected void showBCLibError(MinecraftClient client){
		Obsidian.LOGGER.error("BCLib differs on client and server.");
		client.setScreen(new WarnBCLibVersionMismatch((download) -> {
			MinecraftClient.getInstance().setScreen((Screen)null);
			if (download){
				requestBCLibDownload((hadErrors)->{
					client.stop();
				});
			}
		}));
	}

	@Environment(EnvType.CLIENT)
	protected void showDonwloadConfigs(MinecraftClient client, List<AutoSyncID> files){
		client.setScreen(new SyncFilesScreen((download) -> {
			Minecraft.getInstance().setScreen((Screen)null);
			if (download){
				requestFileDownloads(files);
			}
		}));

	}

	private void requestBCLibDownload(Consumer<Boolean> whenFinished){
		Obsidian.LOGGER.warn("Starting download of BCLib");
		whenFinished.accept(true);
	}

	private void requestFileDownloads(List<AutoSyncID> files){
		Obsidian.LOGGER.info("Starting download of Files:" + files.size());
		DataExchangeAPI.send(new RequestFiles(files));
	}
}