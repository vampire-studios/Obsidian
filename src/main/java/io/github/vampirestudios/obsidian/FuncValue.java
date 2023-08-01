/*
 * Copyright 2021 The Quilt Project
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

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.quiltmc.qsl.registry.attachment.api.DispatchedType;

import java.util.Map;

public abstract class FuncValue implements DispatchedType {
	// in a real-world application, you'd probably use a Registry for this
	public static final Map<ResourceLocation, Codec<? extends FuncValue>> CODECS = Util.make(() ->
			ImmutableMap.<ResourceLocation, Codec<? extends FuncValue>>builder()
					.put(SendMessageFuncValue.TYPE, SendMessageFuncValue.CODEC)
					.put(GiveStackFuncValue.TYPE, GiveStackFuncValue.CODEC)
					.put(TestFuncValue.TYPE, TestFuncValue.CODEC)
					.build()
	);

	protected final ResourceLocation type;

	protected FuncValue(ResourceLocation type) {
		this.type = type;
	}

	@Override
	public final ResourceLocation getType() {
		return this.type;
	}

	public abstract void invoke(ServerPlayer player);

	@Override
	public abstract String toString();
}