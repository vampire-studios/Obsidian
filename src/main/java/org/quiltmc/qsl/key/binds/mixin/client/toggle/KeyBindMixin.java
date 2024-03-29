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

package org.quiltmc.qsl.key.binds.mixin.client.toggle;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.quiltmc.qsl.key.binds.api.ToggleableKeyBind;
import org.quiltmc.qsl.key.binds.impl.KeyBindRegistryImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(KeyMapping.class)
public abstract class KeyBindMixin implements ToggleableKeyBind {
	@Shadow
	@Final
	private static Map<String, KeyMapping> ALL;

	@Unique
	private static final List<KeyMapping> QUILT$ALL_KEY_BINDS = new ArrayList<>();

	@Unique
	private int quilt$disableCounter;

	@Shadow
	public abstract String getName();

	@Shadow
	protected abstract void release();

	@Inject(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V", at = @At("RETURN"))
	private void initializeToggleFields(String string, InputConstants.Type type, int i, String string2, CallbackInfo ci) {
		for (KeyMapping otherKey : QUILT$ALL_KEY_BINDS) {
			if (this.equals(otherKey)) {
				throw new IllegalArgumentException(String.format("%s has already been registered!", this.getName()));
			} else if (this.getName().equals(otherKey.getName())) {
				throw new IllegalArgumentException(String.format("Attempted to register {}, but a key bind with the same translation key has already been registered!", this.getName()));
			}
		}

		KeyBindRegistryImpl.registerKeyBind((KeyMapping) (Object) this);
		quilt$disableCounter = 0;
	}

	@Override
	public boolean isEnabled() {
		return quilt$disableCounter == 0;
	}

	@Override
	public boolean isDisabled() {
		return quilt$disableCounter > 0;
	}

	@Override
	public void enable() {
		// Hahahahaha no.
		if (quilt$disableCounter <= 0) return;

		quilt$disableCounter--;
		if (quilt$disableCounter == 0) {
			KeyBindRegistryImpl.updateKeyBindState((KeyMapping) (Object) this);
			ALL.put(this.getName(), (KeyMapping) (Object) this);
			KeyMapping.resetMapping();
			this.release();
		}
	}

	@Override
	public void disable() {
		quilt$disableCounter++;
		if (quilt$disableCounter == 1) {
			KeyBindRegistryImpl.updateKeyBindState((KeyMapping) (Object) this);
			ALL.remove(this.getName(), (KeyMapping) (Object) this);
			KeyMapping.resetMapping();
			this.release();
		}
	}
}