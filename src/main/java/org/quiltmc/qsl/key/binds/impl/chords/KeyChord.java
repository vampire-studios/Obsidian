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

package org.quiltmc.qsl.key.binds.impl.chords;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2BooleanAVLTreeMap;
import java.util.SortedMap;

public class KeyChord {
	// TODO - Private this, add methods for getting/modifying it
	public SortedMap<InputConstants.Key, Boolean> keys = new Object2BooleanAVLTreeMap<>();

	public KeyChord(SortedMap<InputConstants.Key, Boolean> keys) {
		this.keys = keys;
	}

	public KeyChord() { }

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && o instanceof KeyChord keyChord) {
			return this.keys.keySet().equals(keyChord.keys.keySet());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return keys.keySet().hashCode();
	}
}