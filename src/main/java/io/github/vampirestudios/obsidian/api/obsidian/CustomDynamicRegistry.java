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

package io.github.vampirestudios.obsidian.api.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.function.Supplier;

/**
 * A nicer wrapper around {@link net.minecraft.util.registry.DynamicRegistryManager}.
 *
 * @param <T> the type that the registry holds
 */
public class CustomDynamicRegistry<T> {
    private final SimpleRegistry<T> registry;
    private final Supplier<T> defaultValueSupplier;
    private final Codec<T> codec;

    public CustomDynamicRegistry(SimpleRegistry<T> registry, Supplier<T> defaultValueSupplier, Codec<T> codec) {
        this.registry = registry;
        this.defaultValueSupplier = defaultValueSupplier;
        this.codec = codec;
    }

    public SimpleRegistry<T> getRegistry() {
        return this.registry;
    }

    public RegistryKey<? extends Registry<T>> getRegistryRef() {
        return this.registry.getKey();
    }

    public Lifecycle getLifecycle() {
        return this.registry.getLifecycle();
    }

    public Supplier<T> getDefaultValueSupplier() {
        return this.defaultValueSupplier;
    }

    public Codec<T> getCodec() {
        return this.codec;
    }
} 