/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.fabric.mixin.conditionalresource;

import net.fabricmc.fabric.impl.conditionalresource.NamespaceResourceManagerExtensions;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
	@Shadow
	@Final
	private Map<String, NamespaceResourceManager> namespaceManagers;

	@Inject(method = "reload", at = @At("HEAD"))
	private void beginMonitoredReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> listeners, CallbackInfoReturnable<ResourceReload> cir) {
		for (NamespaceResourceManager manager : namespaceManagers.values()) {
			((NamespaceResourceManagerExtensions) manager).fabric_indexFabricMeta();
		}
	}
}