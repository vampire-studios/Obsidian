/*
package io.github.vampirestudios.obsidian;

import io.netty.buffer.Unpooled;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.config.v1.FabricSaveTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.config.ConfigValueSender;
import net.fabricmc.loader.api.config.ConfigDefinition;
import net.fabricmc.loader.api.config.ConfigManager;
import net.fabricmc.loader.api.config.value.ValueContainer;
import net.fabricmc.loader.config.ValueContainerProviders;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.loader.api.config.data.SaveType;

public class ConfigNetworking implements ModInitializer, ClientModInitializer {
	public static final Identifier SYNC_CONFIG = new Identifier("fabric", "packet/sync_values");
	public static final Identifier USER_CONFIG = new Identifier("fabric", "packet/sync_values/user");

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ClientLoginNetworking.registerGlobalReceiver(SYNC_CONFIG, ConfigReceivers::receiveLevelConfigValues);
		ClientLoginNetworking.registerGlobalReceiver(USER_CONFIG, ConfigReceivers::receiveUserConfigValues);

		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(SYNC_CONFIG, ConfigReceivers::receiveLevelConfigValues);
			ClientPlayNetworking.registerReceiver(USER_CONFIG, ConfigReceivers::receiveUserConfigValues);

			ValueContainer valueContainer = ValueContainerProviders.getInstance(FabricSaveTypes.USER).getValueContainer();

			for (ConfigDefinition<?> configDefinition : ConfigManager.getConfigKeys()) {
				if (!Obsidian.perWorld) {
					ConfigSenders.sendToServer(configDefinition, valueContainer);
				}
			}
		});
	}

	@Override
	public void onInitialize() {
		ServerLoginNetworking.registerGlobalReceiver(USER_CONFIG, ConfigReceivers::handleUserSyncResponse);
		ServerLoginNetworking.registerGlobalReceiver(SYNC_CONFIG, ConfigReceivers::handleLevelSyncResponse);

		ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
			ValueContainer valueContainer = ValueContainerProviders.getInstance(FabricSaveTypes.LEVEL).getValueContainer();

			for (ConfigDefinition<?> configDefinition : ConfigManager.getConfigKeys()) {
				if (Obsidian.perWorld) {
					ConfigSenders.sendToPlayer(configDefinition, sender, valueContainer);
				} else if (!Obsidian.perWorld) {
					PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

					buf.writeString(configDefinition.toString());
					buf.writeString(configDefinition.getVersion().toString());

					sender.sendPacket(USER_CONFIG, buf);
				}
			}
		});

		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			ServerPlayNetworking.registerReceiver(handler, SYNC_CONFIG, ConfigReceivers::receiveConfigValues);
			((ConfigValueSender) server).sendCached(handler.player);
		}));

		ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) ->
				((ConfigValueSender) server).drop(handler.player)));
	}
}*/
