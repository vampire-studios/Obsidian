package io.github.vampirestudios.obsidian.addon_modules.nexo;

import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperBlockExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.minecraft.oraxen.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class NexoItems implements AddonModule {

	// base directory for recursion
	private static Path BASE_PATH = null;
	private static final List<FurnitureBlock> FURNITURE_BLOCKS = new ArrayList<>();

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id)
			throws IOException, SyntaxError, DeserializationException {
		if (!Objects.equals(id.format(), "nexo_like")) return;

		// Initialize BASE_PATH on first call
		if (BASE_PATH == null) {
			if (file.isDirectory()) {
				BASE_PATH = file.toPath();
			} else if (file.getParentFile() != null) {
				BASE_PATH = file.getParentFile().toPath();
			}
		}

		// If directory, recurse and return
		if (file.isDirectory()) {
			try (Stream<Path> paths = Files.walk(BASE_PATH)) {
				paths.filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
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

		// 2) Standard YAML -> Java deserialization
		SimpleModule module = new SimpleModule();
		module.addDeserializer(DataComponentPatch.class, new JacksonDataComponentPatchDeserializer());
		module.addDeserializer(Identifier.class, new JacksonIdentifierDeserializer());
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.registerModule(module);
		mapper.findAndRegisterModules();

		try {
			Map<String, NexoItem> items = mapper.readValue(
					file,
					new TypeReference<>() {}
			);

			for (Map.Entry<String, NexoItem> entry : items.entrySet()) {
				String key = entry.getKey();
				NexoItem nexoItem = entry.getValue();
				if (nexoItem == null) continue;

				Identifier itemId = Identifier.fromNamespaceAndPath(id.modId(), key);
				nexoItem.id = itemId;
				nexoItem.pack.id = itemId;

				// build item properties
				RegistryHelperItemExpanded helper = new RegistryHelperItemExpanded(id.modId());
				RegistryHelperBlockExpanded helperBlock = new RegistryHelperBlockExpanded(id.modId());
				Item.Properties props = new Item.Properties()
						.setId(ResourceKey.create(Registries.ITEM, itemId));

				if (nexoItem.mechanics != null && nexoItem.mechanics.durability != null) {
					props.durability(nexoItem.mechanics.durability.value());
				}

				// apply data components
				if (nexoItem.components != null) {
					for (TypedDataComponent<?> dataComponent : nexoItem.components) {
						applyTyped(props, dataComponent);
					}
				}

				// apply attribute modifiers
				if (nexoItem.mechanics != null && nexoItem.mechanics.attributes != null) {
					props.component(
							DataComponents.ATTRIBUTE_MODIFIERS,
							nexoItem.mechanics.attributes.createAttributeModifiers(
									nexoItem.mechanics.attributes.modifiers
							)
					);
				}

				if (nexoItem.mechanics != null && nexoItem.mechanics.furniture != null) {
					BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.WOOD)
							.setId(ResourceKey.create(Registries.BLOCK, itemId));
					// a) make the block
					FurnitureBlock block;
					/*if (nexoItem.mechanics.furniture.beds != null) {
						block = new BedFurnitureBlock(nexoItem.mechanics.furniture, properties);
					} else */if (nexoItem.mechanics.furniture.connectable != null) {
						block = new ConnectableFurnitureBlock(nexoItem.mechanics.furniture, properties);
					} else {
						block = new FurnitureBlock(nexoItem.mechanics.furniture, properties);
					}
					// b) register in the item registry
					if (!BuiltInRegistries.BLOCK.containsKey(itemId))
						helperBlock.registerBlock(block, key);
					if (!BuiltInRegistries.ITEM.containsKey(itemId))
						helper.registerItem(key, new FurnitureItemImpl(block, nexoItem, props));
				} else {
					// register the item
					if (!BuiltInRegistries.ITEM.containsKey(itemId)) {
						Item item = helper.registerItem(key, determineItemInstance(nexoItem, props));
						// add to creative tab
						if (!nexoItem.excludeFromInventory) {
							ResourceKey<CreativeModeTab> tab = ResourceKey.create(
									Registries.CREATIVE_MODE_TAB,
									Identifier.fromNamespaceAndPath(id.modId(), "items")
							);
							CreativeModeTabEvents.modifyOutputEvent(tab)
									.register(e -> e.accept(item));
						}

						// adaptive resistance hook
						ServerLivingEntityEvents.ALLOW_DAMAGE
								.register((entity, source, amt) -> {
									if (nexoItem.mechanics != null
											&& nexoItem.mechanics.adaptive_resistance != null) {
										nexoItem.mechanics.adaptive_resistance.onEntityDamage(entity, source);
									}
									return true;
								});
					}
				}

				// finally, register in ORAXEN_ITEMS registry
				register(
						ContentRegistries.NEXO_ITEMS,
						"nexo_item",
						nexoItem.id,
						nexoItem
				);
			}

		} catch (Exception e) {
			failedRegistering("nexo_item", file.getName(), e);
		}
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
