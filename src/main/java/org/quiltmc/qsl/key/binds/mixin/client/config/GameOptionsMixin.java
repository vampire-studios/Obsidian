/*
 * Copyright 2022 QuiltMC
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

package org.quiltmc.qsl.key.binds.mixin.client.config;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
	@Inject(
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/option/GameOptions;write()V"
			),
			method = "setKeyCode"
	)
	private void writeToKeyBindConfig(KeyBinding key, InputUtil.Key code, CallbackInfo ci) {
//		QuiltKeyBindsConfigManager.updateConfig(false);
	}

	@Redirect(
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/option/KeyBinding;setBoundKey(Lnet/minecraft/client/util/InputUtil$Key;)V"
			),
			method = "accept"
	)
	private void useOurConfigInstead(KeyBinding keyBind, InputUtil.Key key) { }
}