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

package org.quiltmc.qsl.block.content.registry.impl.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public class BlockContentRegistriesClientInitializer implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("BlockContentRegistriesClientInitializer");

	public static final String ENABLE_TOOLTIP_DEBUG = "quilt.block.block_content_registry.enable_tooltip_debug";

	@Override
	public void onInitializeClient() {
		if (Boolean.getBoolean(ENABLE_TOOLTIP_DEBUG) || FabricLoader.getInstance().isModLoaded("quilt_block_content_registry_testmod")) {
			ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
				Block block = Block.byItem(stack.getItem());

				BlockContentRegistries.FLATTENABLE.get(block).ifPresent(state -> lines.add(Component.literal("Flattenable block: " + state)));
				BlockContentRegistries.OXIDIZABLE.get(block).ifPresent(_block -> lines.add(Component.literal("Oxidizes to: " + _block.block())));
				BlockContentRegistries.WAXABLE.get(block).ifPresent(_block -> lines.add(Component.literal("Waxes to: " + _block.block())));
				BlockContentRegistries.STRIPPABLE.get(block).ifPresent(_block -> lines.add(Component.literal("Strips to: " + _block)));
				BlockContentRegistries.FLAMMABLE.get(block).ifPresent(entry -> lines.add(Component.literal("Flammable: " + entry.burn() + " burn chance, " + entry.spread() + " spread chance")));
				BlockContentRegistries.ENCHANTING_BOOSTERS.get(block).ifPresent(value -> lines.add(Component.literal("Enchanting booster: " + value)));
			});
		}
	}
}