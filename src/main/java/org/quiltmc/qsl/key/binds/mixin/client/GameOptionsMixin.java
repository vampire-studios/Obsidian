/*
 * Copyright 2021-2022 QuiltMC
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

package org.quiltmc.qsl.key.binds.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.quiltmc.qsl.key.binds.impl.InternalQuiltKeyBind;
import org.quiltmc.qsl.key.binds.impl.KeyBindManager;
import org.quiltmc.qsl.key.binds.impl.KeyBindRegistryImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Environment(EnvType.CLIENT)
@Mixin(Options.class)
public abstract class GameOptionsMixin {
	@Shadow
	@Mutable
	@Final
	public KeyMapping[] keyMappings;

	@Shadow
	@Final
	private File optionsFile;

	@Inject(
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/Options;load()V"
			),
			method = "<init>"
	)
	private void modifyAllKeys(Minecraft client, File file, CallbackInfo ci) {
		if (this.optionsFile.equals(new File(file, "options.txt"))) {
			// Mark the Vanilla key binds as Vanilla
			for (KeyMapping key : this.keyMappings) {
				((InternalQuiltKeyBind) key).markAsVanilla();
			}

			KeyBindRegistryImpl.setKeyBindManager(new KeyBindManager((Options) (Object) this, this.keyMappings));
			this.keyMappings = KeyBindRegistryImpl.getKeyBinds();
		}
	}
}