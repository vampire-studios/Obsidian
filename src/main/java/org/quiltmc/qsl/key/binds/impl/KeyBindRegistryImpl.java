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

package org.quiltmc.qsl.key.binds.impl;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public class KeyBindRegistryImpl {
	public static final Logger LOGGER = LoggerFactory.getLogger("KeyBindRegistry");

	private static final List<KeyMapping> ALL_KEY_BINDS = new ReferenceArrayList<>();
	private static final List<KeyMapping> ENABLED_KEYS = new ReferenceArrayList<>();
	private static KeyBindManager keyBindManager = null;

	public static KeyMapping getKeyBind(String translationKey) {
		for (KeyMapping key : ALL_KEY_BINDS) {
			if (key.getName().equals(translationKey)) {
				return key;
			}
		}

		return null;
	}

	public static List<KeyMapping> getAllKeyBinds() {
		return ALL_KEY_BINDS;
	}

	public static void registerKeyBind(KeyMapping key) {
		ALL_KEY_BINDS.add(key);
		ENABLED_KEYS.add(key);
	}

	public static void updateKeyBindState(KeyMapping key) {
		if (key.isEnabled()) {
			ENABLED_KEYS.add(key);
		} else {
			ENABLED_KEYS.remove(key);
		}

		if (keyBindManager != null) {
			keyBindManager.addModdedKeyBinds();
		}

		//QuiltKeyBindsConfigManager.CONFIG.save();
	}

	public static KeyMapping[] getKeyBinds() {
		return ENABLED_KEYS.toArray(new KeyMapping[0]);
	}

	public static void setKeyBindManager(KeyBindManager manager) {
		keyBindManager = manager;
	}
}