package io.github.vampirestudios.obsidian.addon_modules.nexo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperBlockExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.minecraft.oraxen.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.util.Unit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class NexoItems implements AddonModule {

	private static final List<FurnitureBlock> FURNITURE_BLOCKS = new ArrayList<>();

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id)
			throws IOException {
		if (!Objects.equals(id.format(), "nexo_like")) return;

		// If directory, recurse and return
		if (file.isDirectory()) {
			try (Stream<java.nio.file.Path> paths = Files.walk(file.toPath())) {
				paths.filter(p -> AddonFormats.isSupported(p.toFile()))
						.forEach(p -> {
							try {
								init(addon, p.toFile(), id);
							} catch (Exception e) {
								failedRegistering("oraxen_item", p.getFileName().toString(), e);
							}
						});
			}
			return;
		}

		// 2) Standard config -> Java deserialization
		SimpleModule module = new SimpleModule();
		module.addDeserializer(DataComponentPatch.class, new JacksonDataComponentPatchDeserializer());
		module.addDeserializer(DataComponentMap.class, new JacksonDataComponentMapDeserializer());
		module.addDeserializer(NexoItem.Mechanics.Attributes.class, new JacksonAttributeModifiersDeserializer());
		module.addDeserializer(Identifier.class, new JacksonIdentifierDeserializer());
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		mapper.findAndRegisterModules();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

		try {
			JsonNode root = mapper.readTree(AddonFormats.readAsJsonString(addon, file));
			if (root == null || !root.isObject()) {
				throw new IOException("Expected a map of Nexo items");
			}

			for (Map.Entry<String, JsonNode> entry : root.properties()) {
				try {
					registerItem(mapper, entry.getKey(), entry.getValue(), id);
				} catch (Exception exception) {
					failedRegistering("nexo_item", file.getName() + ":" + entry.getKey(), exception);
				}
			}
		} catch (Exception e) {
			failedRegistering("nexo_item", file.getName(), e);
		}
	}

	private void registerItem(ObjectMapper mapper, String key, JsonNode node, BasicAddonInfo id) throws IOException {
		// Has to happen on the tree: once a path becomes an Identifier, "foo" and "minecraft:foo" are
		// the same value and there is no way back to knowing which the author wrote.
		NexoPackPaths.qualify(node, id.modId());

		NexoItem nexoItem = mapper.treeToValue(node, NexoItem.class);
		if (nexoItem == null) return;

		Identifier itemId = Identifier.fromNamespaceAndPath(id.modId(), key);
		nexoItem.id = itemId;
		if (nexoItem.pack == null) nexoItem.pack = new NexoItem.Pack();
		nexoItem.pack.id = itemId;
		if (!nexoItem.pack.generate_model && nexoItem.pack.model == null
				&& (nexoItem.pack.parent_model != null || nexoItem.pack.texture != null || nexoItem.pack.textures != null)) {
			nexoItem.pack.generate_model = true;
		}
		if (nexoItem.material == null || nexoItem.material.isBlank()) nexoItem.material = "PAPER";
		nexoItem.material = nexoItem.material.toUpperCase(java.util.Locale.ROOT);

		RegistryHelperItemExpanded helper = new RegistryHelperItemExpanded(id.modId());
		RegistryHelperBlockExpanded helperBlock = new RegistryHelperBlockExpanded(id.modId());
		Item.Properties props = new Item.Properties()
				.setId(ResourceKey.create(Registries.ITEM, itemId));

		if (nexoItem.mechanics != null && nexoItem.mechanics.durability != null) {
			props.durability(nexoItem.mechanics.durability.value());
		}
		if (nexoItem.unbreakable) props.component(DataComponents.UNBREAKABLE, Unit.INSTANCE);

		if (nexoItem.components != null) {
			for (TypedDataComponent<?> dataComponent : nexoItem.components) {
				applyTyped(props, dataComponent);
			}
		}
		if (nexoItem.itemModel != null && !nexoItem.itemModel.isNull()
				&& (nexoItem.components == null || nexoItem.components.get(DataComponents.ITEM_MODEL) == null)) {
			props.component(DataComponents.ITEM_MODEL, itemId);
		}
		NexoItem.Mechanics.Attributes attributes = nexoItem.attributeModifiers;
		if (attributes == null && nexoItem.mechanics != null) attributes = nexoItem.mechanics.attributes;
		if (attributes != null && attributes.modifiers != null) {
			props.component(DataComponents.ATTRIBUTE_MODIFIERS,
					attributes.createAttributeModifiers(attributes.modifiers));
		}

		Item registeredItem = null;
		if (nexoItem.mechanics != null && nexoItem.mechanics.furniture != null) {
					SoundType sound = nexoItem.mechanics.furniture.block_sounds != null
							? nexoItem.mechanics.furniture.block_sounds.getSoundType() : SoundType.WOOD;
					BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().strength(1.5f).sound(sound)
							.setId(ResourceKey.create(Registries.BLOCK, itemId));
					// a) make the block
					FurnitureBlock block;
					boolean stateful = nexoItem.mechanics.furniture.states != null
							&& !nexoItem.mechanics.furniture.states.isEmpty();
					if (nexoItem.mechanics.furniture.connectable != null) {
						block = stateful
								? new StatefulConnectableFurnitureBlock(nexoItem.mechanics.furniture, properties)
								: new ConnectableFurnitureBlock(nexoItem.mechanics.furniture, properties);
					} else {
						block = stateful ? new StatefulFurnitureBlock(nexoItem.mechanics.furniture, properties)
								: new FurnitureBlock(nexoItem.mechanics.furniture, properties);
					}
					// b) register in the item registry
					if (!BuiltInRegistries.BLOCK.containsKey(itemId)) {
						helperBlock.registerBlock(block, key);
						FURNITURE_BLOCKS.add(block);
					}
					if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
						registeredItem = helper.registerItem(key, new FurnitureItemImpl(block, nexoItem, props));
					}
		} else if (hasCustomBlock(nexoItem)) {
			NexoItem.Mechanics.CustomBlock mechanic = customBlockMechanic(nexoItem);
			float hardness = mechanic != null && mechanic.hardness > 0 ? mechanic.hardness : 1.5F;
			float resistance = mechanic != null && mechanic.blast_resistant ? 1200.0F : hardness;
			SoundType sound = mechanic != null && mechanic.block_sounds != null
					? mechanic.block_sounds.getSoundType() : SoundType.WOOD;
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
					.strength(hardness, resistance).sound(sound)
					.setId(ResourceKey.create(Registries.BLOCK, itemId));
			Block block = new NexoCustomBlock(mechanic, properties);
			if (!BuiltInRegistries.BLOCK.containsKey(itemId)) helperBlock.registerBlock(block, key);
			if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
				registeredItem = helper.registerItem(key, new NexoCustomBlockItemImpl(block, mechanic, props));
			}
		} else {
					// register the item
					if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
						registeredItem = helper.registerItem(key, determineItemInstance(nexoItem, props));

						// adaptive resistance hook
						if (nexoItem.mechanics != null && nexoItem.mechanics.adaptive_resistance != null) {
							ServerLivingEntityEvents.ALLOW_DAMAGE
								.register((entity, source, amt) -> {
									nexoItem.mechanics.adaptive_resistance.onEntityDamage(entity, source);
									return true;
								});
						}
					}
		}

		if (registeredItem != null && !nexoItem.excludeFromInventory) {
			ResourceKey<CreativeModeTab> tab = ResourceKey.create(
					Registries.CREATIVE_MODE_TAB,
					Identifier.fromNamespaceAndPath(id.modId(), "items")
			);
			Item creativeItem = registeredItem;
			CreativeModeTabEvents.modifyOutputEvent(tab).register(output -> output.accept(creativeItem));
		}

		register(
						ContentRegistries.NEXO_ITEMS,
						"nexo_item",
						nexoItem.id,
						nexoItem
				);
	}

	private static boolean hasCustomBlock(NexoItem item) {
		return item.mechanics != null && (item.mechanics.custom_block != null
				|| item.mechanics.noteblock != null || item.mechanics.stringblock != null);
	}

	private static NexoItem.Mechanics.CustomBlock customBlockMechanic(NexoItem item) {
		if (item.mechanics.custom_block != null) return item.mechanics.custom_block;
		if (item.mechanics.noteblock != null) return item.mechanics.noteblock;
		return item.mechanics.stringblock;
	}

	@SuppressWarnings("unchecked")
	private static <T> void applyTyped(Item.Properties props, TypedDataComponent<T> entry) {
		props.component(entry.type(), entry.value());
	}

	@Override
	public String getType() {
		return "items";
	}

	/**
	 * Chooses the correct Item subclass based on Mechanics.equipable, dyeable, etc.
	 */
	private Item determineItemInstance(NexoItem ni, Item.Properties props) {
		if (ni.mechanics != null) {
			if (ni.mechanics.dyeable != null) return new DyeableItemImpl(ni, props);
			if (ni.mechanics.equipable != null) return new EquipableItemImpl(ni, props);
		}
		return ni.getItem(props);
	}
}
