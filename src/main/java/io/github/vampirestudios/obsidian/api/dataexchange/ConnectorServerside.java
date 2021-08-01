package io.github.vampirestudios.obsidian.api.dataexchange;

import io.github.vampirestudios.obsidian.Obsidian;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

class ConnectorServerside extends Connector {
	private MinecraftServer server;
	ConnectorServerside(DataExchangeAPI api) {
		super(api);
		server = null;
	}

	@Override
	public boolean onClient() {
		return false;
	}

	protected void onPlayInit(ServerPlayNetworkHandler handler, MinecraftServer server){
		if (this.server!=null && this.server != server){
			Obsidian.LOGGER.warn("Server changed!");
		}
		this.server = server;
		for(DataHandlerDescriptor desc : getDescriptors()){
			ServerPlayNetworking.registerReceiver(handler, desc.IDENTIFIER, (_server, _player, _handler, _buf, _responseSender) -> {
				receiveFromClient(desc, _server, _player, _handler, _buf, _responseSender);
			});
		}
	}

	void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
		for(DataHandlerDescriptor desc : getDescriptors()){
			if (desc.sendOnJoin){
				DataHandler h = desc.JOIN_INSTANCE.get();
				if (h.getOriginatesOnServer()) {
					h.sendToClient(server, handler.player);
				}
			}
		}
	}

	void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server){
		for(DataHandlerDescriptor desc : getDescriptors()){
			ServerPlayNetworking.unregisterReceiver(handler, desc.IDENTIFIER);
		}
	}

	void receiveFromClient(DataHandlerDescriptor desc, MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
		DataHandler h = desc.INSTANCE.get();
		h.receiveFromClient(server, player, handler, buf, responseSender);
	}

	void sendToClient(DataHandler h){
		if (server==null){
			throw new RuntimeException("[internal error] Server not initialized yet!");
		}
		h.sendToClient(this.server);
	}
}