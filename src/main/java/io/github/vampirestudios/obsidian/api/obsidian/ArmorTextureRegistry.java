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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Objects;

/**
 * A class for registering custom armor textures for {@link ItemConvertible}, to be provided by a {@link Provider}.
 *
 * <p>
 * This can be used to replace existing vanilla armor textures conditionally, however each {@link net.minecraft.item.Item}
 * instance can only allow one {@link Provider}.
 * </p>
 *
 * <p>
 * Since armor textures identifier in vanilla is hardcoded to be in the {@code minecraft} namespace, this registry can also be
 * used to use a custom namespace if desired.
 * </p>
 */
@Environment(EnvType.CLIENT)
public final class ArmorTextureRegistry {
	private ArmorTextureRegistry() {
	}

	/**
	 * Registers a provider for custom texture models for an item.
	 *
	 * @param provider the provider for the texture
	 * @param items    the items to be registered for
	 */
	public static void register(/* @Nullable */ Provider provider, ItemConvertible... items) {
		register(provider, Arrays.asList(items));
	}

	/**
	 * Registers a provider for custom texture models for an item.
	 *
	 * @param provider the provider for the texture
	 * @param items    the items to be registered for
	 */
	public static void register(/* @Nullable */ Provider provider, Iterable<ItemConvertible> items) {
		ArmorTextureRegistryImpl.register(provider, Objects.requireNonNull(items));
	}

	/**
	 * Gets the armor texture identifier in string, to be converted to {@link net.minecraft.util.Identifier}.
	 *
	 * @param entity      The entity equipping the armor
	 * @param stack       The item stack of the armor
	 * @param slot        The slot which the armor is in
	 * @param secondLayer Whether the texture should use the second layer, only true when the {@code slot} is {@link EquipmentSlot#LEGS}
	 * @param suffix      The nullable suffix of the texture, may be "overlay" in vanilla when the item is {@link net.minecraft.item.DyeableArmorItem}
	 * @return the custom armor texture identifier, return null to use the vanilla ones. Defaulted to the item's registry id.
	 */
	/* @Nullable */
	public static String getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, boolean secondLayer, /* @Nullable */  String suffix) {
		return ArmorTextureRegistryImpl.getArmorTexture(entity, stack, slot, secondLayer, suffix);
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface Provider {
		/**
		 * Gets the armor texture identifier in string, to be converted to {@link net.minecraft.util.Identifier}.
		 *
		 * @param entity      The entity equipping the armor
		 * @param stack       The item stack of the armor
		 * @param slot        The slot which the armor is in
		 * @param secondLayer Whether the texture should use the second layer, only true when the {@code slot} is {@link EquipmentSlot#LEGS}
		 * @param suffix      The nullable suffix of the texture, may be "overlay" in vanilla when the item is {@link net.minecraft.item.DyeableArmorItem}
		 * @return the custom armor texture identifier, return null to use the vanilla ones. Defaulted to the item's registry id.
		 */
		/* @Nullable */
		String getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, boolean secondLayer, /* @Nullable */  String suffix);
	}
}