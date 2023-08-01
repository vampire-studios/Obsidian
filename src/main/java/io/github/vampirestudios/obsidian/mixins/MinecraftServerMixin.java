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

package io.github.vampirestudios.obsidian.mixins;

import com.google.common.collect.ImmutableList;
import io.github.vampirestudios.obsidian.ResourceLoaderEventContextsImpl;
import io.github.vampirestudios.obsidian.ResourceLoaderEvents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Shadow
	public abstract ResourceManager getResourceManager();

	@Shadow
	public abstract RegistryAccess.Frozen registryAccess();

	@Inject(
			method = "method_29437(Lnet/minecraft/core/RegistryAccess$Frozen;Lcom/google/common/collect/ImmutableList;)Ljava/util/concurrent/CompletionStage;",
			at = @At("RETURN"),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onReloadResourcesStart(
			RegistryAccess.Frozen frozen, ImmutableList packs, CallbackInfoReturnable<CompletionStage> cir,
			CloseableResourceManager currentResourceManager
	) {
		ResourceLoaderEventContextsImpl.server = new WeakReference<>((MinecraftServer) (Object) this);
		ResourceLoaderEvents.START_DATA_PACK_RELOAD.invoker().onStartDataPackReload(new ResourceLoaderEventContextsImpl.ReloadStartContext(
				() -> currentResourceManager, this.getResourceManager()
		));
	}

	@Inject(method = "reloadResources", at = @At("TAIL"))
	private void onReloadResourcesEnd(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		cir.getReturnValue().handleAsync((value, throwable) -> {
			ResourceLoaderEvents.END_DATA_PACK_RELOAD.invoker().onEndDataPackReload(new ResourceLoaderEventContextsImpl.ReloadEndContext(
					this.getResourceManager(), this.registryAccess(), Optional.ofNullable(throwable)
			));
			return value;
		}, (MinecraftServer) (Object) this);
	}
}