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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class SendMessageFuncValue extends FuncValue {
	public static final ResourceLocation TYPE = new ResourceLocation("quilt", "send_message");
	public static final Codec<SendMessageFuncValue> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(Codec.STRING.fieldOf("message").forGetter(sm -> sm.message))
					.apply(instance, SendMessageFuncValue::new));

	private final String message;

	public SendMessageFuncValue(String message) {
		super(TYPE);
		this.message = message;
	}

	@Override
	public void invoke(ServerPlayer player) {
		player.sendSystemMessage(Component.literal("Quilt says: '" + this.message + "'"), false);
	}

	@Override
	public String toString() {
		return "send_message{'" + this.message + "'}";
	}
}