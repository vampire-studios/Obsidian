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

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;
import org.quiltmc.qsl.registry.attachment.api.RegistryExtensions;
import org.slf4j.Logger;

public class DispatchedAttachmentTest implements ModInitializer {
	public static final RegistryEntryAttachment<Item, FuncValue> MODULAR_FUNCTION = RegistryEntryAttachment.dispatchedBuilder(
			BuiltInRegistries.ITEM,
			Identifier.fromNamespaceAndPath("quilt", "modular_function"),
			FuncValue.class, FuncValue.CODECS::get
	).build();

	public static final Logger LOGGER = LogUtils.getLogger();

	public static final class ModularFunctionItem extends Item {
		public ModularFunctionItem(Item.Properties settings) {
			super(settings);
		}

		@Override
		public InteractionResult use(Level level, Player user, InteractionHand usedHand) {
			if (!level.isClientSide()) {
				ServerPlayer player = (ServerPlayer) user;
				MODULAR_FUNCTION.get(this).ifPresentOrElse(funcValue -> funcValue.invoke(player),
						() -> player.sendSystemMessage(Component.literal("No function assigned!")
								.withStyle(ChatFormatting.RED), true));
			}

			return InteractionResult.PASS;
		}
	}

	/**
	 * Has a built-in value of one type.
	 */
	public static final ModularFunctionItem ITEM_1 = register("modular_item_1");
	/**
	 * Has a built-in value of one type, overridden via datapack by a value with another type.
	 */
	public static final ModularFunctionItem ITEM_2 = register("modular_item_2");
	/**
	 * Set via datapack.
	 */
	public static final ModularFunctionItem ITEM_3 = register1("modular_item_3");
	/**
	 * Has no value at all.
	 */
	public static final ModularFunctionItem ITEM_4 = register1("modular_item_4");
	/**
	 * Has a value a provided by a tag.
	 */
	public static final ModularFunctionItem ITEM_5 = register1("modular_item_5");
	/**
	 * Has a value a provided by a tag via datapack.
	 */
	public static final ModularFunctionItem ITEM_6 = register1("modular_item_6");

	private static <V1> ModularFunctionItem register(String name) {
		var id = Identifier.fromNamespaceAndPath("quilt", name);
		return RegistryExtensions.register(BuiltInRegistries.ITEM, id, new ModularFunctionItem(new Item.Properties()
				.setId(ResourceKey.create(Registries.ITEM, id))
		), DispatchedAttachmentTest.MODULAR_FUNCTION, new SendMessageFuncValue("Built-in value!"));
	}

	private static ModularFunctionItem register1(String name) {
		var id = Identifier.fromNamespaceAndPath("quilt", name);
		return Registry.register(BuiltInRegistries.ITEM, id, new ModularFunctionItem(new Item.Properties()
				.setId(ResourceKey.create(Registries.ITEM, id))
		));
	}

	@Override
	public void onInitialize() {
		MODULAR_FUNCTION.put(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("quilt", "modular_tag_1")),
				new SendMessageFuncValue("Built-in value via tag!"));

		ResourceLoaderEvents.END_DATA_PACK_RELOAD.register(context -> {
			if (context.error().isPresent()) return;

			LOGGER.info(" === DATA PACK RELOADED! === ");

			var tagIt = MODULAR_FUNCTION.tagEntryIterator();
			while (tagIt.hasNext()) {
				var entry = tagIt.next();
				LOGGER.info("Tag #{} is set to {}", entry.tag().location(), entry.value());
			}

			var it = MODULAR_FUNCTION.entryIterator();
			while (it.hasNext()) {
				var entry = it.next();
				LOGGER.info("Entry {} is set to {}", BuiltInRegistries.ITEM.getKey(entry.entry()), entry.value());
			}
		});
	}
}