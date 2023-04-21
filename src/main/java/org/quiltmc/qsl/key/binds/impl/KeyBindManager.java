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

package org.quiltmc.qsl.key.binds.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.key.binds.mixin.client.GameOptionsAccessor;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public class KeyBindManager {
	private final Options options;
	private final KeyMapping[] allKeys;

	public KeyBindManager(Options options, KeyMapping[] allKeys) {
		this.options = options;
		this.allKeys = allKeys;
	}

	public void addModdedKeyBinds() {
		((GameOptionsAccessor) this.options).setAllKeys(KeyBindRegistryImpl.getKeyBinds());
	}

	public KeyMapping[] getAllKeys() {
		return this.allKeys;
	}
}