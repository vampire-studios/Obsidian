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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.registry.attachment.impl.reloader.RegistryEntryAttachmentReloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public final class Initializer implements ModInitializer {
	public static final String NAMESPACE = "quilt_registry_entry_attachments";

	public static final String ENABLE_DUMP_BUILTIN_COMMAND_PROPERTY =
			"quilt.data.registry_entry_attachment.enable_dump_builtin_command";

	public static final Logger LOGGER = LoggerFactory.getLogger("QuiltRegistryEntryAttachment");

	public static ResourceLocation id(String path) {
		return new ResourceLocation(NAMESPACE, path);
	}

	private static MinecraftServer server;

	@Override
	public void onInitialize() {
		RegistryEntryAttachmentReloader.register(PackType.SERVER_DATA);
		RegistryEntryAttachmentSync.register();

		ServerLifecycleEvents.SERVER_STARTING.register(server1 -> server = server1);
		ServerLifecycleEvents.SERVER_STARTED.register(server1 -> {
			if (server == server1) {
				server = null;
			}
		});

		if (Boolean.getBoolean(ENABLE_DUMP_BUILTIN_COMMAND_PROPERTY)) {
			/*if (QuiltLoader.isModLoaded("quilt_command")) {
				DumpBuiltinAttachmentsCommand.register();
			} else {
				LOGGER.warn("Property \"{}\" was set to true, but required module \"quilt_command\" is missing!",
						ENABLE_DUMP_BUILTIN_COMMAND_PROPERTY);
			}*/
		}
	}

	public static @Nullable MinecraftServer getServer() {
		return server;
	}
}