package io.github.vampirestudios.obsidian.api.dataexchange;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public abstract class DataHandler {
	private final boolean originatesOnServer;
	@NotNull
	private final Identifier identifier;

	protected DataHandler(Identifier identifier, boolean originatesOnServer){
		this.originatesOnServer = originatesOnServer;
		this.identifier = identifier;
	}

	final public boolean getOriginatesOnServer(){
		return originatesOnServer;
	}

	final public @NotNull Identifier getIdentifier(){
		return identifier;
	}

	@Environment(EnvType.CLIENT)
	void receiveFromServer(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
		deserializeFromIncomingData(buf, responseSender, false);
		client.execute(() -> runOnClient(client));
	}

	void receiveFromClient(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
		deserializeFromIncomingData(buf, responseSender, true);
		server.execute(() -> runOnServer(server));
	}

	protected void deserializeFromIncomingData(PacketByteBuf buf, PacketSender responseSender, boolean fromClient){
	}

	@Environment(EnvType.CLIENT)
	protected void runOnClient(MinecraftClient client){

	}

	protected void runOnServer(MinecraftServer server){

	}

	protected void serializeData(PacketByteBuf buf) {

	}

	void sendToClient(MinecraftServer server){
		PacketByteBuf buf = PacketByteBufs.create();
		serializeData(buf);

		for (ServerPlayerEntity player : PlayerLookup.all(server)) {
			ServerPlayNetworking.send(player, this.identifier, buf);
		}
	}

	void sendToClient(MinecraftServer server, ServerPlayerEntity player){
		PacketByteBuf buf = PacketByteBufs.create();
		serializeData(buf);
		ServerPlayNetworking.send(player, this.identifier, buf);
	}

	@Environment(EnvType.CLIENT)
	void sendToServer(MinecraftClient client){
		PacketByteBuf buf = PacketByteBufs.create();
		serializeData(buf);
		ClientPlayNetworking.send(identifier, buf);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DataHandler that = (DataHandler) o;
		return originatesOnServer == that.originatesOnServer && identifier.equals(that.identifier);
	}

	@Override
	public int hashCode() {
		int hash = identifier.hashCode();
		if (originatesOnServer) hash |= 0x80000000;
		else hash &=0x7FFFFFFF;

		return hash;
	}

	@Override
	public String toString() {
		return "DataHandler{" + "originatesOnServer=" + originatesOnServer + ", identifier=" + identifier + '}';
	}

	/**
	 * Write a String to a buffer (Convenience Method)
	 * @param buf The buffer to write to
	 * @param s The String you want to write
	 */
	public static void writeString(PacketByteBuf buf, String s){
		buf.writeByteArray(s.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Read a string from a buffer (Convenience Method)
	 * @param buf Thea buffer to read from
	 * @return The received String
	 */
	public static String readString(PacketByteBuf buf){
		byte[] data = buf.readByteArray();
		return new String(data, StandardCharsets.UTF_8);
	}
}