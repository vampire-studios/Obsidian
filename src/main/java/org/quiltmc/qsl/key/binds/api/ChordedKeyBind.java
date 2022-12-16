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

package org.quiltmc.qsl.key.binds.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.quiltmc.qsl.key.binds.impl.chords.KeyChord;

@Environment(EnvType.CLIENT)
public interface ChordedKeyBind {
	default KeyChord getBoundChord() {
		throw new UnsupportedOperationException();
	}

	default void setBoundChord(KeyChord chord) { }

	// TODO - This is a temporary measure until CHASM comes. Replace it with a proper constructor or builder
	default KeyBinding withChord(InputUtil.Key... keys) {
		throw new UnsupportedOperationException();
	}
}