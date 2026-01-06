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

package org.quiltmc.qsl.registry.attachment.impl.reloader;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;
import org.quiltmc.qsl.registry.attachment.impl.ClientSideGuard;
import org.quiltmc.qsl.registry.attachment.impl.Initializer;
import org.quiltmc.qsl.registry.attachment.impl.RegistryEntryAttachmentHolder;
import org.quiltmc.qsl.registry.attachment.impl.RegistryEntryAttachmentSync;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@ApiStatus.Internal
public final class RegistryEntryAttachmentReloader implements SimpleResourceReloadListener<RegistryEntryAttachmentReloader.LoadedData> {

	public static void register(PackType source) {
//		if (source == PackType.SERVER_DATA) {
//			ResourceManagerHelper.get(source).addReloadListener(ResourceReloaderKeys.Server.TAGS, ID_DATA);
//		}

		ResourceManagerHelper.get(source).addReloadListener(new RegistryEntryAttachmentReloader(source));
	}

	static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier ID_DATA = Identifier.fromNamespaceAndPath(Initializer.NAMESPACE, "data");
	private static final Identifier ID_ASSETS = Identifier.fromNamespaceAndPath(Initializer.NAMESPACE, "assets");

	private final PackType source;
	private final Identifier id;

	private RegistryEntryAttachmentReloader(PackType source) {
		if (source == PackType.CLIENT_RESOURCES) {
			ClientSideGuard.assertAccessAllowed();
		}

		this.source = source;
		this.id = switch (source) {
			case SERVER_DATA -> ID_DATA;
			case CLIENT_RESOURCES -> ID_ASSETS;
		};
	}

	@Override
	public @NotNull Identifier getFabricId() {
		return this.id;
	}

	@Override
	public CompletableFuture<LoadedData> load(ResourceManager manager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			var attachDicts = new HashMap<RegistryEntryAttachment<?, ?>, AttachmentDictionary<?, ?>>();

			for (var entry : BuiltInRegistries.REGISTRY.entrySet()) {
				Identifier registryId = entry.getKey().identifier();
				String path = registryId.getNamespace() + "/" + registryId.getPath();

				Map<Identifier, List<Resource>> resources = manager.listResourceStacks("attachments/" + path,
						s -> s.getPath().endsWith(".json"));
				if (resources.isEmpty()) {
					continue;
				}

				Registry<?> registry = entry.getValue();
				this.processResources(attachDicts, resources, registry);
			}

			return new LoadedData(attachDicts);
		}, executor);
	}

	private void processResources(
			Map<RegistryEntryAttachment<?, ?>, AttachmentDictionary<?, ?>> attachDicts,
			Map<Identifier, List<Resource>> resources, Registry<?> registry) {
		for (var entry : resources.entrySet()) {
			Identifier attachmentId = this.getAttachmentId(entry.getKey());
			RegistryEntryAttachment<?, ?> attachment = RegistryEntryAttachmentHolder.getAttachment(registry, attachmentId);
			if (attachment == null) {
				LOGGER.warn("Unknown attachment {} (from {})", attachmentId, entry);
				continue;
			}

			if (!attachment.side().shouldLoad(this.source)) {
				LOGGER.warn("Ignoring attachment {} (from {}) since it shouldn't be loaded from this source ({}, we're loading from {})",
						attachmentId, entry, attachment.side().getSource(), this.source);
				continue;
			}

			AttachmentDictionary<?, ?> attachDict = attachDicts.computeIfAbsent(attachment, this::createAttachmentMap);
			for (var resource : entry.getValue()) {
				attachDict.processResource(entry.getKey(), resource);
			}
		}
	}

	private <R, V> AttachmentDictionary<R, V> createAttachmentMap(RegistryEntryAttachment<R, V> attachment) {
		return new AttachmentDictionary<>(attachment.registry(), attachment);
	}

	@Override
	public CompletableFuture<Void> apply(LoadedData data, ResourceManager manager, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			data.apply();
			if (this.source == PackType.SERVER_DATA) {
				RegistryEntryAttachmentSync.clearEncodedValuesCache();
				RegistryEntryAttachmentSync.syncAttachmentsToAllPlayers();
			}
		}, executor);
	}

	// "<namespace>:attachments/<path>/<file_name>.json" becomes "<namespace>:<file_name>"
	private Identifier getAttachmentId(Identifier jsonId) {
		String path = jsonId.getPath();
		int lastSlash = path.lastIndexOf('/');
		path = path.substring(lastSlash + 1);

		int lastDot = path.lastIndexOf('.');
		path = path.substring(0, lastDot);
		return Identifier.fromNamespaceAndPath(jsonId.getNamespace(), path);
	}

	protected final class LoadedData {
		private final Map<RegistryEntryAttachment<?, ?>, AttachmentDictionary<?, ?>> attachmentMaps;

		private LoadedData(Map<RegistryEntryAttachment<?, ?>, AttachmentDictionary<?, ?>> attachmentMaps) {
			this.attachmentMaps = attachmentMaps;
		}

		@SuppressWarnings("unchecked")
		public void apply() {
			for (var entry : BuiltInRegistries.REGISTRY.entrySet()) {
				RegistryEntryAttachmentHolder.getData(entry.getValue())
						.prepareReloadSource(RegistryEntryAttachmentReloader.this.source);
			}

			for (var entry : this.attachmentMaps.entrySet()) {
				this.applyOne((RegistryEntryAttachment<Object, Object>) entry.getKey(), (AttachmentDictionary<Object, Object>) entry.getValue());
			}
		}

		@SuppressWarnings("unchecked")
		private <R, V> void applyOne(RegistryEntryAttachment<R, V> attachment, AttachmentDictionary<R, V> attachAttachment) {
			var registry = attachment.registry();
			Objects.requireNonNull(registry, "registry");

			RegistryEntryAttachmentHolder<R> holder = RegistryEntryAttachmentHolder.getData(registry);
			for (Map.Entry<AttachmentDictionary.ValueTarget, Object> attachmentEntry : attachAttachment.getMap().entrySet()) {
				V value = (V) attachmentEntry.getValue();
				AttachmentDictionary.ValueTarget target = attachmentEntry.getKey();
				switch (target.type()) {
					case ENTRY -> holder.putValue(attachment, registry.getValue(target.id()), value);
					case TAG -> holder.putValue(attachment, TagKey.create(registry.key(), target.id()), value);
					default -> throw new IllegalStateException("Unexpected value: " + target.type());
				}
			}
		}
	}
}