package io.github.vampirestudios.obsidian.api.dataexchange;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.utils.ModVersionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HelloServer extends DataHandler {
	public static DataHandlerDescriptor DESCRIPTOR = new DataHandlerDescriptor(new Identifier(Obsidian.MOD_ID, "hello_server"), HelloServer::new, true);

	public HelloServer() {
		super(DESCRIPTOR.IDENTIFIER, false);
	}

	public static String getModVersion(String modID){
		Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(modID);
		if (optional.isPresent()) {
			ModContainer modContainer = optional.get();
			return modContainer.getMetadata().getVersion().toString();
		}
		return "0.0.0";
	}

	protected static String getBCLibVersion(){
		return getModVersion(Obsidian.MOD_ID);
	}

	String bclibVersion ="0.0.0";
	Map<String, String> modVersion = new HashMap<>();
	@Override
	protected void deserializeFromIncomingData(PacketByteBuf buf, PacketSender responseSender, boolean fromClient) {
		bclibVersion = ModVersionUtils.getModVersion(buf.readInt());
		modVersion = new HashMap<>();

		int count = buf.readInt();
		for (int i=0; i< count; i++){
			String id = readString(buf);
			String version = ModVersionUtils.getModVersion(buf.readInt());
			modVersion.put(id, version);
		}
	}

	@Override
	protected void runOnServer(MinecraftServer server) {
		String localBclibVersion = getBCLibVersion();
		Obsidian.LOGGER.info("Hello Server received from BCLib. (server="+localBclibVersion+", client="+bclibVersion+")");

		for (Map.Entry<String, String> e : modVersion.entrySet()){
			String ver = getModVersion(e.getKey());
			Obsidian.LOGGER.info("    - " + e.getKey() + " (server="+ver+", client="+ver+")");
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void runOnClient(MinecraftClient client) {

	}

	@Override
	protected void serializeData(PacketByteBuf buf) {
		final List<String> mods = DataExchangeAPI.registeredMods();
		buf.writeInt(ModVersionUtils.getModVersion(getBCLibVersion()));

		buf.writeInt(mods.size());
		for (String modID : mods) {
			writeString(buf, modID);
			buf.writeInt(ModVersionUtils.getModVersion(getModVersion(modID)));
		}
	}

}