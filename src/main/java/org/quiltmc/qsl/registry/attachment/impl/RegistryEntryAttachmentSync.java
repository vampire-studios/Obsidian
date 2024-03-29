/*
 * Copyright 2021 The Quilt Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.registry.attachment.impl;

import io.github.vampirestudios.vampirelib.api.FriendlyByteBufs;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;

import java.util.*;

import static org.quiltmc.qsl.registry.attachment.impl.Initializer.id;

@ApiStatus.Internal
public final class RegistryEntryAttachmentSync {
	/**
	 * Indicates the packet version.
	 * <p>
	 * This value should be updated whenever packet formats are changed.
	 */
	private static final byte PACKET_VERSION = 1;

	private RegistryEntryAttachmentSync() {
	}

	public static final ResourceLocation PACKET_ID = id("sync");

	private record NamespaceValuePair(String namespace, Set<AttachmentEntry> entries) {
	}

	private record CacheEntry(ResourceLocation registryId, Set<NamespaceValuePair> namespacesToValues) {
	}

	private record AttachmentEntry(String path, boolean isTag, Tag value) {
		public void write(FriendlyByteBuf buf) {
			buf.writeUtf(this.path);
			buf.writeBoolean(this.isTag);

			CompoundTag compound = new CompoundTag();
			compound.put("value", this.value);
			buf.writeNbt(compound);
		}

		public static AttachmentEntry read(FriendlyByteBuf buf) {
			String path = buf.readUtf();
			boolean isTag = buf.readBoolean();
			Tag value = buf.readNbt().get("value");

			return new AttachmentEntry(path, isTag, value);
		}
	}

	public static final Map<ResourceLocation, CacheEntry> ENCODED_VALUES_CACHE = new Object2ReferenceOpenHashMap<>();

	public static void register() {
		ServerPlayConnectionEvents.JOIN.register(RegistryEntryAttachmentSync::syncAttachmentsToPlayer);
	}

	@Environment(EnvType.CLIENT)
	public static void registerClient() {
		ClientPlayNetworking.registerGlobalReceiver(PACKET_ID, RegistryEntryAttachmentSync::receiveSyncPacket);
	}

	public static List<FriendlyByteBuf> createSyncPackets() {
		fillEncodedValuesCache();
		var bufs = new ArrayList<FriendlyByteBuf>();

		for (var entry : ENCODED_VALUES_CACHE.entrySet()) {
			for (var valueMap : entry.getValue().namespacesToValues()) {
				var buf = FriendlyByteBufs.create();
				buf.writeByte(PACKET_VERSION);
				buf.writeResourceLocation(entry.getValue().registryId());
				buf.writeResourceLocation(entry.getKey());
				buf.writeUtf(valueMap.namespace());
				buf.writeInt(valueMap.entries().size());
				for (AttachmentEntry attachmentEntry : valueMap.entries()) {
					attachmentEntry.write(buf);
				}

				bufs.add(buf);
			}
		}

		return bufs;
	}

	public static void syncAttachmentsToAllPlayers() {
		var server = Initializer.getServer();

		if (server == null) {
			return;
		}

		for (var player : server.getPlayerList().getPlayers()) {
			if (isPlayerLocal(player)) continue;

			for (var buf : createSyncPackets()) {
				ServerPlayNetworking.send(player, PACKET_ID, buf);
			}
		}
	}

	private static boolean isPlayerLocal(ServerPlayer player) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return player.getStringUUID().equals(Minecraft.getInstance().getUser().getUuid());
		}

		return false;
	}

	public static void clearEncodedValuesCache() {
		ENCODED_VALUES_CACHE.clear();
	}

	@SuppressWarnings("unchecked")
	private static void fillEncodedValuesCache() {
		if (!ENCODED_VALUES_CACHE.isEmpty()) {
			return;
		}

		for (var registryEntry : BuiltInRegistries.REGISTRY.entrySet()) {
			var registry = (Registry<Object>) registryEntry.getValue();
			var dataHolder = RegistryEntryAttachmentHolder.getData(registry);

			for (var attachmentEntry : RegistryEntryAttachmentHolder.getAttachmentEntries(registry)) {
				var attachment = (RegistryEntryAttachment<Object, Object>) attachmentEntry.getValue();
				if (attachment.side() != RegistryEntryAttachment.Side.BOTH) {
					continue;
				}

				// Namespace, Attachment
				var encoded = new HashMap<String, Set<AttachmentEntry>>();
				Map<Object, Object> entryValues = dataHolder.valueTable.rowMap().get(attachmentEntry.getValue());
				if (entryValues != null) {
					for (var valueEntry : entryValues.entrySet()) {
						var entryId = registry.getKey(valueEntry.getKey());
						if (entryId == null) {
							throw new IllegalStateException("Foreign object in data holder of attachment %s: %s"
									.formatted(attachment.id(), valueEntry.getKey()));
						}

						encoded.computeIfAbsent(entryId.getNamespace(), id -> new HashSet<>()).add(
								new AttachmentEntry(entryId.getPath(), false, attachment.codec()
										.encodeStart(NbtOps.INSTANCE, valueEntry.getValue())
										.getOrThrow(false, msg -> {
											throw new IllegalStateException("Failed to encode value for attachment %s of registry entry %s: %s"
													.formatted(attachment.id(), entryId, msg));
										})
								)
						);
					}
				}

				Map<TagKey<Object>, Object> entryTagValues = dataHolder.valueTagTable.rowMap().get(attachmentEntry.getValue());
				if (entryTagValues != null) {
					for (var valueEntry : entryTagValues.entrySet()) {
						encoded.computeIfAbsent(valueEntry.getKey().location().getNamespace(), id -> new HashSet<>()).add(
								new AttachmentEntry(valueEntry.getKey().location().getPath(), true, attachment.codec()
										.encodeStart(NbtOps.INSTANCE, valueEntry.getValue())
										.getOrThrow(false, msg -> {
											throw new IllegalStateException("Failed to encode value for attachment tag %s of registry %s: %s"
													.formatted(attachment.id(), valueEntry.getKey().location(), msg));
										})));
					}
				}

				var valueMaps = new HashSet<NamespaceValuePair>();
				for (var namespaceEntry : encoded.entrySet()) {
					valueMaps.add(new NamespaceValuePair(namespaceEntry.getKey(), namespaceEntry.getValue()));
				}

				ENCODED_VALUES_CACHE.put(attachment.id(), new CacheEntry(attachment.registry().key().location(), valueMaps));
			}
		}
	}

	private static void syncAttachmentsToPlayer(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
		if (isPlayerLocal(handler.getPlayer())) return;

		for (var buf : RegistryEntryAttachmentSync.createSyncPackets()) {
			sender.sendPacket(RegistryEntryAttachmentSync.PACKET_ID, buf);
		}
	}

	@Environment(EnvType.CLIENT)
	@SuppressWarnings("unchecked")
	private static void receiveSyncPacket(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		var packetVersion = buf.readByte();
		if (packetVersion != PACKET_VERSION) {
			throw new UnsupportedOperationException("Unable to read RegistryEntryAttachmentSync packet. Please install the same version of QSL as the server you play on");
		}

		var registryId = buf.readResourceLocation();
		var attachmentId = buf.readResourceLocation();
		var namespace = buf.readUtf();

		var size = buf.readInt();
		var attachments = new HashSet<AttachmentEntry>();

		while (size > 0) {
			attachments.add(AttachmentEntry.read(buf));
			size--;
		}

		client.execute(() -> {
			var registry = (Registry<Object>) BuiltInRegistries.REGISTRY.get(registryId);
			if (registry == null) {
				throw new IllegalStateException("Unknown registry %s".formatted(registryId));
			}

			var attachment = (RegistryEntryAttachment<Object, Object>) RegistryEntryAttachmentHolder.getAttachment(registry, attachmentId);
			if (attachment == null) {
				throw new IllegalStateException("Unknown attachment %s for registry %s".formatted(attachmentId, registryId));
			}

			var holder = RegistryEntryAttachmentHolder.getData(registry);
			holder.valueTable.row(attachment).clear();
			holder.valueTagTable.row(attachment).clear();

			for (AttachmentEntry attachmentEntry : attachments) {
				var entryId = new ResourceLocation(namespace, attachmentEntry.path);

				var registryObject = registry.get(entryId);
				if (registryObject == null) {
					throw new IllegalStateException("Foreign ID %s".formatted(entryId));
				}

				var parsedValue = attachment.codec()
						.parse(NbtOps.INSTANCE, attachmentEntry.value)
						.getOrThrow(false, msg -> {
							throw new IllegalStateException("Failed to decode value for attachment %s of registry entry %s: %s"
									.formatted(attachment.id(), entryId, msg));
						});

				if (attachmentEntry.isTag) {
					holder.putValue(attachment, TagKey.create(registry.key(), entryId), parsedValue);
				} else {
					holder.putValue(attachment, registryObject, parsedValue);
				}
			}
		});
	}
}