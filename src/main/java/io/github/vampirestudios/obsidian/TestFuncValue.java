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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class TestFuncValue extends FuncValue {
	public static final ResourceLocation TYPE = new ResourceLocation("quilt", "test");
	public static final Codec<TestFuncValue> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(ItemStack.CODEC.fieldOf("stack").forGetter(gs -> gs.stack))
					.apply(instance, TestFuncValue::new));

	private final ItemStack stack;

	public TestFuncValue(ItemStack stack) {
		super(TYPE);
		this.stack = stack;
	}

	@Override
	public void invoke(ServerPlayer player) {
		player.getInventory().placeItemBackInInventory(this.stack.copy());
	}

	@Override
	public String toString() {
		return "test{" + BuiltInRegistries.ITEM.getId(this.stack.getItem()) + " x" + this.stack.getCount() + "}";
	}
}