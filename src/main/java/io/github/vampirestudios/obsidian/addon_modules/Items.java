package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.ItemDisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.item.ItemInformation;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.minecraft.CustomMenuItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BlockItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BundleItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Items implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		if (!"obsidian".equals(id.format())) return;

		io.github.vampirestudios.obsidian.api.obsidian.item.Item item = loadItem(addon, file);

		if (item == null) return;

		applyTemplate(item);

		try {
			Identifier identifier = getIdentifier(item, id, file);
			Item.Properties settings = createItemProperties(item).setId(ResourceKey.create(Registries.ITEM, identifier));
			RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());
			ResourceKey<CreativeModeTab> creativeTab = getCreativeTab(item);

			registerEvents(item);

			registerItem(expanded, item, identifier, settings, creativeTab);

			register(ContentRegistries.ITEMS, "item", identifier, item);
		} catch (Exception e) {
			failedRegistering("item", file.getName(), e);
		}
	}

	private io.github.vampirestudios.obsidian.api.obsidian.item.Item loadItem(IAddonPack addon, File file) throws IOException, SyntaxError {
		io.github.vampirestudios.obsidian.api.obsidian.item.Item item;
		if (addon.getConfigPackInfo() instanceof LegacyObsidianAddonInfo) {
			item = BaseGson.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
		} else {
			ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addon.getConfigPackInfo();
			item = switch (addonInfo.format) {
				case JSON ->
						BaseGson.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
				case JSON5 -> {
					JsonObject jsonObject = Jankson.builder().build().load(file);
					yield Jankson.builder().build().fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
				}
				case YAML ->
						new ObjectMapper(new YAMLFactory()).readValue(file, io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
				case TOML ->
						new ObjectMapper(new TomlFactory()).readValue(file, io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
				case HJSON ->
						BaseGson.GSON.fromJson(JsonValue.readHjson(new FileReader(file)).toString(Stringify.FORMATTED), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
				default -> null;
			};
		}
		return item;
	}

	private Identifier getIdentifier(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, BasicAddonInfo id, File file) {
		Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replace(".json", ""));
		item.information.id = identifier;
		return identifier;
	}

	@SuppressWarnings("unchecked")
	static <T> void applyAllComponents(Item.Properties props, DataComponentPatch map) {
		for (var e : map.entrySet()) {
			var type = (DataComponentType<T>) e.getKey();
			var opt  = (Optional<T>) e.getValue();
			opt.ifPresent(v -> props.component(type, v));
		}
	}

	private Item.Properties createItemProperties(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		Item.Properties props = new Item.Properties();

		var settings = item.information.getItemSettings();
		if (settings != null) {
			props.stacksTo(settings.maxStackSize)
					.rarity(net.minecraft.world.item.Rarity.valueOf(settings.rarity.toUpperCase(java.util.Locale.ROOT)));

			if (settings.durability != 0) props.durability(settings.durability);
			if (settings.fireproof) props.fireResistant();
			if (settings.tooltipStyle != null) {
				props.component(net.minecraft.core.component.DataComponents.TOOLTIP_STYLE,
						settings.tooltipStyle);
			}
		}

		if (item.components != null) applyAllComponents(props, item.components);

		return props;
	}

	private ResourceKey<CreativeModeTab> getCreativeTab(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		ResourceKey<CreativeModeTab> creativeTab;

		// Check for OItemComponents.CREATIVE_TAB first
		if (item.components != null && item.components.get(OItemComponents.CREATIVE_TAB) != null &&
				item.components.get(OItemComponents.CREATIVE_TAB).isPresent()) {
			Identifier tabLocation = (Identifier) Objects.requireNonNull(item.components.get(OItemComponents.CREATIVE_TAB)).orElseThrow();
			creativeTab = ResourceKey.create(Registries.CREATIVE_MODE_TAB, tabLocation);
		} else if (item.information.getItemSettings().getItemGroup() != null) {
			creativeTab = item.information.getItemSettings().getItemGroup();
		} else if (item.information.getItemSettings().getParentSettings().getItemGroup() != null) {
			creativeTab = item.information.getItemSettings().getParentSettings().getItemGroup();
		} else {
			creativeTab = CreativeModeTabs.BUILDING_BLOCKS;
		}
		return creativeTab;
	}

	private void registerEvents(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		if (item.type == io.github.vampirestudios.obsidian.api.obsidian.item.Item.ItemType.SHEARS && item.drops != null && !item.drops.isEmpty()) {
			UseEntityCallback.EVENT.register((player, level, hand, entity, _) -> {
				Map<EntityType<?>, Item> shearDrops = item.drops.entrySet().stream()
						.map(entry -> {
							EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(entry.getKey());
							Item shearDropItem = BuiltInRegistries.ITEM.getValue(entry.getValue());
							return new AbstractMap.SimpleEntry<>(entityType, shearDropItem);
						})
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
				if (shearDrops.containsKey(entity.getType())) {
					// Create an ItemStack for the item to be dropped
					ItemStack dropStack = new ItemStack(shearDrops.get(entity.getType()), 1);
					entity.spawnAtLocation((ServerLevel) level, dropStack);
					player.getItemInHand(hand).hurtAndBreak(1, player, hand.asEquipmentSlot());
					return InteractionResult.SUCCESS;
				}
				return InteractionResult.PASS;
			});
		}
	}

	private Item registerItem(RegistryHelperItemExpanded expanded, io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Identifier identifier,
							  Item.Properties settings, ResourceKey<CreativeModeTab> creativeTab) {
		Item registeredItem;
		if (item.information.getItemSettings().canPlaceBlock) {
			registeredItem = expanded.registerItem(identifier, new BlockItemImpl(item, BuiltInRegistries.BLOCK.getValue(item.information.getItemSettings().placableBlock), settings), creativeTab);
		} else {
			if (item.type == io.github.vampirestudios.obsidian.api.obsidian.item.Item.ItemType.BUNDLE) {
				registeredItem = expanded.registerItem(identifier.getPath(), new BundleItem(item, settings), creativeTab);
			} else if (item.type == io.github.vampirestudios.obsidian.api.obsidian.item.Item.ItemType.CUSTOM_MENU) {
				registeredItem = expanded.registerItem(identifier.getPath(), new CustomMenuItem(item, settings), creativeTab);
			} else {
				registeredItem = expanded.registerItem(identifier.getPath(), new ItemImpl(item, settings), creativeTab);
			}
		}
		return registeredItem;
	}

	private void applyTemplate(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		if (item.template == null || item.template.isBlank()) return;

		net.minecraft.resources.Identifier templateId = net.minecraft.resources.Identifier.tryParse(item.template);
		if (templateId == null) {
			Obsidian.LOGGER.warn("Item has invalid template reference: {}", item.template);
			return;
		}

		io.github.vampirestudios.obsidian.api.obsidian.item.Item template =
				ContentRegistries.ITEM_TEMPLATES.getValue(templateId);
		if (template == null) {
			Obsidian.LOGGER.warn("Item references unknown template: {}", templateId);
			return;
		}

		if (item.type == null) item.type = template.type;
		if (item.useActions == null) item.useActions = template.useActions;
		if (item.components == null) item.components = template.components;
		if (item.menuConfig == null) item.menuConfig = template.menuConfig;

		// Lore: use template's if item defines none
		if ((item.lore == null || item.lore.isEmpty()) && template.lore != null) {
			item.lore = template.lore;
		}

		// Maps: template provides defaults, item values take priority
		if (template.drops != null) template.drops.forEach(item.drops::putIfAbsent);
		if (template.events != null) template.events.forEach(item.events::putIfAbsent);

		// Information: merge sub-fields so the item keeps its own name
		if (item.information == null) {
			item.information = template.information;
		} else {
			mergeInformation(item.information, template.information);
		}

		// Rendering: per-field null-fallback to template
		if (item.rendering == null) {
			item.rendering = template.rendering;
		} else {
			mergeRendering(item.rendering, template.rendering);
		}
	}

	private void mergeInformation(ItemInformation item, ItemInformation template) {
		if (template == null) return;
		if (item.itemSettings == null) item.itemSettings = template.itemSettings;
		if (item.itemType == null) item.itemType = template.itemType;
		// item.name is intentionally not merged — each item must have its own identity
	}

	private void mergeRendering(ItemDisplayInformation item, ItemDisplayInformation template) {
		if (template == null) return;
		if (item.itemModel == null) item.itemModel = template.itemModel;
		if (item.blockingModel == null) item.blockingModel = template.blockingModel;
		if (item.pullingModels == null) item.pullingModels = template.pullingModels;
		if (item.chargedModel == null) item.chargedModel = template.chargedModel;
		if (item.fireworkModel == null) item.fireworkModel = template.fireworkModel;
		if (item.castModel == null) item.castModel = template.castModel;
		if (item.throwingModel == null) item.throwingModel = template.throwingModel;
		if (item.arrowModel == null) item.arrowModel = template.arrowModel;
		if (item.brokenModel == null) item.brokenModel = template.brokenModel;
		if (item.damagedModels == null) item.damagedModels = template.damagedModels;
		if (item.cooldownModel == null) item.cooldownModel = template.cooldownModel;
		if (item.chargingModels == null) item.chargingModels = template.chargingModels;
		if (item.useModels == null) item.useModels = template.useModels;
		if (item.binarySelects == null) item.binarySelects = template.binarySelects;
	}

	@Override
	public String getType() {
		return Utils.elementsDirPath(Registries.ITEM);
	}
}
