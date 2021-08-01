package io.github.vampirestudios.obsidian.api.dataexchange;

import io.github.vampirestudios.obsidian.Obsidian;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
class ConnectorClientside extends Connector {
	private MinecraftClient client;
	ConnectorClientside(DataExchangeAPI api) {
		super(api);
		this.client = null;
	}


	@Override
	public boolean onClient() {
		return true;
	}

	protected void onPlayInit(ClientPlayNetworkHandler handler, MinecraftClient client){
		if (this.client!=null && this.client != client){
			Obsidian.LOGGER.warn("Client changed!");
		}
		this.client = client;
		for(DataHandlerDescriptor desc : getDescriptors()){
			ClientPlayNetworking.registerReceiver(desc.IDENTIFIER, (_client, _handler, _buf, _responseSender)->{
				receiveFromServer(desc, _client, _handler, _buf, _responseSender);
			});
		}
	}

	void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client){
		for(DataHandlerDescriptor desc : getDescriptors()){
			if (desc.sendOnJoin){
				DataHandler h = desc.JOIN_INSTANCE.get();
				if (!h.getOriginatesOnServer()) {
					h.sendToServer(client);
				}
			}
		}
	}

	void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client){
		for(DataHandlerDescriptor desc : getDescriptors()) {
			ClientPlayNetworking.unregisterReceiver(desc.IDENTIFIER);
		}
	}

	void receiveFromServer(DataHandlerDescriptor desc, MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
		DataHandler h = desc.INSTANCE.get();
		h.receiveFromServer(client, handler, buf, responseSender);
	}

	void sendToServer(DataHandler h){
		if (client==null){
			throw new RuntimeException("[internal error] Client not initialized yet!");
		}
		h.sendToServer(this.client);
	}
}