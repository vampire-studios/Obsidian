package io.github.vampirestudios.obsidian.api.dataexchange;

import io.github.vampirestudios.obsidian.Obsidian;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class TransferContentPacks extends DataHandler {
	public static DataHandlerDescriptor DESCRIPTOR = new DataHandlerDescriptor(new Identifier(Obsidian.MOD_ID, "transfer_content_packs"), TransferContentPacks::new, true);

	public TransferContentPacks() {
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

	protected static String getBCLibVersion(){
		return getModVersion(Obsidian.MOD_ID);
	}

	@Override
	protected void deserializeFromIncomingData(PacketByteBuf buf, PacketSender responseSender, boolean fromClient) {

	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void runOnClient(MinecraftClient client) {

	}

	@Override
	protected void serializeData(PacketByteBuf buf) {

	}

	private static final String raa_datapackUrl = "https://launcher.mojang.com/v1/objects/622bf0fd298e1e164ecd05d866045ed5941283cf/CavesAndCliffsPreview.zip";

	private static final File raa_datapackLocation = new File("config/CavesAndCliffsPreview.zip");

	private static void raa_addDatapack(String worldName) throws IOException {
		if(!raa_datapackLocation.exists()) {
			try(BufferedInputStream in = new BufferedInputStream(new URL(raa_datapackUrl).openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(raa_datapackLocation)) {
				byte[] dataBuffer = new byte[1024];
				int bytesRead;
				while((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					fileOutputStream.write(dataBuffer, 0, bytesRead);
				}
			} catch(IOException ignored) { }
		}
		if(raa_datapackLocation.exists()) {
			Path dest = Paths.get("saves", worldName, "datapacks/CavesAndCliffsPreview.zip");
			Files.copy(raa_datapackLocation.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
		}
	}

}