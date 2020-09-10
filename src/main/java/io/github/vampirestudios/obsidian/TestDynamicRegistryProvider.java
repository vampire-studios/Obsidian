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

package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.api.CustomDynamicRegistry;
import io.github.vampirestudios.obsidian.api.DynamicRegistryProvider;
import io.github.vampirestudios.obsidian.api.Tater;

import java.util.function.Consumer;

public class TestDynamicRegistryProvider implements DynamicRegistryProvider {
	public void addDynamicRegistries(Consumer<CustomDynamicRegistry<?>> adder) {
		adder.accept(new CustomDynamicRegistry<>(Obsidian.TATER_REGISTRY, () -> Obsidian.DEFAULT_TATER, Tater.CODEC));
	}
} 