/*
 * Copyright 2022 The Quilt Project
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

package org.quiltmc.qsl.block.content.registry.api.enchanting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A constant enchanting booster.
 *
 * @param value the boost level
 */
public record ConstantBooster(float value) implements EnchantingBooster {
	public static final Codec<ConstantBooster> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.FLOAT.fieldOf("value").forGetter(ConstantBooster::value)
			).apply(instance, ConstantBooster::new)
	);
	public static EnchantingBoosterType TYPE = EnchantingBoosters.register(new ResourceLocation("quilt", "constant"), CODEC);

	@Override
	public float getEnchantingBoost(Level world, BlockState state, BlockPos pos) {
		return this.value;
	}

	@Override
	public EnchantingBoosterType getType() {
		return TYPE;
	}
}