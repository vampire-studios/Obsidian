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

package io.github.vampirestudios.obsidian;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

/**
 * Events related to the resource loader.
 */
public final class ResourceLoaderEvents {
	private ResourceLoaderEvents() {
		throw new UnsupportedOperationException("ResourceLoaderEvents only contains static definitions.");
	}

	/**
	 * An event indicating the start of the reloading of data packs on a Minecraft server.
	 * <p>
	 * This event should not be used to load resources, use {@link ResourceManagerHelper#registerReloadListener(IdentifiableResourceReloadListener)} instead.
	 */
	public static final Event<StartDataPackReload> START_DATA_PACK_RELOAD = EventFactory.createArrayBacked(StartDataPackReload.class,
			callbacks -> context -> {
				for (var callback : callbacks) {
					callback.onStartDataPackReload(context);
				}
			});

	/**
	 * An event indicating the end of the reloading of data packs on a Minecraft server.
	 * <p>
	 * This event should not be used to load resources, use {@link ResourceManagerHelper#registerReloadListener(IdentifiableResourceReloadListener)} instead.
	 */
	public static final Event<EndDataPackReload> END_DATA_PACK_RELOAD = EventFactory.createArrayBacked(EndDataPackReload.class,
			callbacks -> context -> {
				for (var callback : callbacks) {
					callback.onEndDataPackReload(context);
				}
				ResourceLoaderEventContextsImpl.server = null;
			});

	public sealed interface DataPackReloadContext permits ResourceLoaderEvents.StartDataPackReload.Context, ResourceLoaderEvents.EndDataPackReload.Context {
		/**
		 * {@return the server instance}
		 */
		@Contract(pure = true)
		MinecraftServer server();

		/**
		 * {@return the resource manager}
		 */
		@Contract(pure = true)
		ResourceManager resourceManager();

		/**
		 * Gets whether the resource reload is the first of the given server lifetime or not.
		 *
		 * @return {@code true} if the resource reload is the first of the given server lifetime, or {@code false otherwise}
		 */
		@Contract(pure = true)
		@SuppressWarnings("resource")
		default boolean isFirst() {
			return this.server() == null;
		}
	}

	/**
	 * Functional interface to be implemented on callbacks for {@link #START_DATA_PACK_RELOAD}.
	 *
	 * @see #START_DATA_PACK_RELOAD
	 */
	@FunctionalInterface
	public interface StartDataPackReload {
		/**
		 * Called before data packs on a Minecraft server have been reloaded.
		 *
		 * @param context the context for the data-pack reload
		 */
		void onStartDataPackReload(Context context);

		@ApiStatus.NonExtendable
		non-sealed interface Context extends DataPackReloadContext {
			/**
			 * {@return the previous resource manager used for previous reloads, may be {@linkplain Optional#empty() empty} for the first reload}
			 */
			@Contract(pure = true)
			Optional<ResourceManager> previousResourceManager();
		}
	}

	/**
	 * Functional interface to be implemented on callbacks for {@link #END_DATA_PACK_RELOAD}.
	 *
	 * @see #END_DATA_PACK_RELOAD
	 */
	@FunctionalInterface
	public interface EndDataPackReload {
		/**
		 * Called after data packs on a Minecraft server have been reloaded.
		 * <p>
		 * If the reload was not successful, the old data packs will be kept.
		 *
		 * @param context the data-pack reload context, if the error is present the data-pack reload failed
		 */
		void onEndDataPackReload(Context context);

		@ApiStatus.NonExtendable
		non-sealed interface Context extends DataPackReloadContext {
			/**
			 * {@return the dynamic registry manager instance}
			 */
			@Contract(pure = true)
			RegistryAccess dynamicRegistries();

			/**
			 * {@return present if the data-pack reload failed, or {@linkplain Optional#empty() empty} otherwise}
			 */
			@Contract(pure = true)
			Optional<Throwable> error();
		}
	}
}