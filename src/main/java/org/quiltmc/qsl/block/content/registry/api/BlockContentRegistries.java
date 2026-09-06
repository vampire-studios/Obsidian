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

package org.quiltmc.qsl.block.content.registry.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBooster;
import org.quiltmc.qsl.block.content.registry.api.enchanting.EnchantingBoosters;
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;

/**
 * Holds {@link RegistryEntryAttachment}s for different properties that blocks can hold.
 * <p>
 * Current properties:
 * <ul>
 *     <li>{@link #FLAMMABLE}</li>
 * 	   <li>{@link #ENCHANTING_BOOSTERS}</li>
 * </ul>
 */
public class BlockContentRegistries {
	/**
	 * The namespace for the content registries.
	 */
	public static final String NAMESPACE = "quilt";

	/**
	 * A {@link RegistryEntryAttachment} for flammable blocks.
	 * <p>
	 * Values can be set via code and through a data-pack with the file {@code data/quilt/attachments/minecraft/block/flammable.json}
	 */
	public static final RegistryEntryAttachment<Block, FlammableBlockEntry> FLAMMABLE = RegistryEntryAttachment
			.builder(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(NAMESPACE, "flammable"), FlammableBlockEntry.class, FlammableBlockEntry.CODEC)
			.build();

	/**
	 * A {@link RegistryEntryAttachment} for enchanting boosters in bookshelf equivalents.
	 * <p>
	 * Values can be set via code and through a data-pack with the file {@code data/quilt/attachments/minecraft/block/enchanting_boosters.json}
	 */
	public static final RegistryEntryAttachment<Block, EnchantingBooster> ENCHANTING_BOOSTERS = RegistryEntryAttachment
			.builder(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(NAMESPACE, "enchanting_boosters"), EnchantingBooster.class, EnchantingBoosters.CODEC)
			.build();
}
