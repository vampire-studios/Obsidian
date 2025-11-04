package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.RenderModeModel;
import io.github.vampirestudios.obsidian.client.ClientInit;
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
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.Equippable;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Items implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		if (!"obsidian".equals(id.format())) return;

		io.github.vampirestudios.obsidian.api.obsidian.item.Item item = loadItem(addon, file);

		if (item == null) return;

		try {
			ResourceLocation identifier = getResourceLocation(item, id, file);
			Item.Properties settings = createItemProperties(item).setId(ResourceKey.create(Registries.ITEM, identifier));
			RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());
			ResourceKey<CreativeModeTab> creativeTab = getCreativeTab(item);

			registerEvents(item);

			Item registeredItem = registerItem(expanded, item, identifier, settings, creativeTab);

			System.out.println(STR."Item: \{registeredItem.components()}");

//			if (item.information.getItemSettings().fuel != null) {
//				FuelRegistry.INSTANCE.add(registeredItem, item.information.getItemSettings().fuel.duration);
//			}

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

	private ResourceLocation getResourceLocation(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, BasicAddonInfo id, File file) {
		ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(id.modId(), file.getName().replace(".json", ""));
		item.information.name.id = identifier;
		return identifier;
	}

	private Item.Properties createItemProperties(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		Item.Properties settings = new Item.Properties();

		if (item.components != null) {
			for (Map.Entry<DataComponentType<?>, Optional<?>> entry : item.components.entrySet()) {
				entry.getValue().ifPresent(value -> settings.component((DataComponentType) entry.getKey(), value));
			}

			Optional<?> maxStackSizeComponent = item.components.get(DataComponents.MAX_STACK_SIZE);
			if (maxStackSizeComponent == null || maxStackSizeComponent.isEmpty()) {
				settings.stacksTo(item.information.getItemSettings().maxStackSize);
			}

			Optional<?> rarityComponent = item.components.get(DataComponents.RARITY);
			if (rarityComponent == null || rarityComponent.isEmpty()) {
				settings.rarity(Rarity.valueOf(item.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)));
			}

			Optional<?> maxDamageComponent = item.components.get(DataComponents.MAX_DAMAGE);
			if (maxDamageComponent == null || maxDamageComponent.isEmpty()) {
				if (item.information.getItemSettings().durability != 0 && item.information.getItemSettings().maxStackSize == 1) {
					settings.durability(item.information.getItemSettings().durability);
				}
			}

			System.out.println(STR."JSON: \{item.components.toString()}");
		}

		/*if (item.information.getItemSettings().fireproof) {
			settings.fireResistant();
		}*/

		return settings;
	}


	private ResourceKey<CreativeModeTab> getCreativeTab(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		ResourceKey<CreativeModeTab> creativeTab;

		// Check for OItemComponents.CREATIVE_TAB first
		if (item.components != null && item.components.get(OItemComponents.CREATIVE_TAB) != null &&
				item.components.get(OItemComponents.CREATIVE_TAB).isPresent()) {
			ResourceLocation tabLocation = (ResourceLocation) Objects.requireNonNull(item.components.get(OItemComponents.CREATIVE_TAB)).orElseThrow();
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
					player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
					return InteractionResult.SUCCESS;
				}
				return InteractionResult.PASS;
			});
		}
        /*UseItemCallback.EVENT.register((player, _, hand) -> {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.getItem() instanceof ItemImpl item1) {
                if (item1.item != item) return InteractionResultHolder.fail(itemStack);
                item1.item.getEventActions("on_use");
                return InteractionResultHolder.success(itemStack);
            }
            return InteractionResultHolder.pass(itemStack);
        });*/
	}

	private Item registerItem(RegistryHelperItemExpanded expanded, io.github.vampirestudios.obsidian.api.obsidian.item.Item item, ResourceLocation identifier,
							  Item.Properties settings, ResourceKey<CreativeModeTab> creativeTab) {
		Item registeredItem;
		if (item.information.getItemSettings().canPlaceBlock) {
			registeredItem = expanded.registerItem(identifier, new BlockItemImpl(item, BuiltInRegistries.BLOCK.getValue(item.information.getItemSettings().placableBlock), settings), creativeTab);
		} else {
			registerRenderModeModels(item);
			if (isWearable(item)) {
				registeredItem = registerWearableItem(expanded, item, identifier, settings, creativeTab);
			} else {
				if (isDyeable(item)) {
					int defaultDyeableColor = 0xFFFFFF;
					if (item.information.getItemSettings().defaultColor != 0) {
						defaultDyeableColor = item.information.getItemSettings().getDefaultColor();
					} else if (item.information.getItemSettings().getParentSettings().defaultColor != 0) {
						defaultDyeableColor = item.information.getItemSettings().getParentSettings().getDefaultColor();
					}
					settings.component(DataComponents.DYED_COLOR, new DyedItemColor(defaultDyeableColor));
				}
				if (item.type == io.github.vampirestudios.obsidian.api.obsidian.item.Item.ItemType.BUNDLE) {
					registeredItem = expanded.registerItem(identifier.getPath(), new BundleItem(item, settings), creativeTab);
				} else if (item.type == io.github.vampirestudios.obsidian.api.obsidian.item.Item.ItemType.CUSTOM_MENU) {
					registeredItem = expanded.registerItem(identifier.getPath(), new CustomMenuItem(item, settings), creativeTab);
				} else {
					registeredItem = expanded.registerItem(identifier.getPath(), new ItemImpl(item, settings), creativeTab);
				}
			}
		}
		return registeredItem;
	}

	private void registerRenderModeModels(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		if (item.information.getItemSettings().renderModeModels != null) {
			for (RenderModeModel renderModeModel : item.information.getItemSettings().renderModeModels) {
				if (!renderModeModel.modes.isEmpty()) {
					for (String mode : renderModeModel.modes) {
						ClientInit.customModels.add(new ModelLayerLocation(renderModeModel.model, mode));
					}
				} else {
					ClientInit.customModels.add(new ModelLayerLocation(renderModeModel.model, "inventory"));
				}
			}
		}
	}

	private boolean isWearable(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		return (item.information.getItemSettings().wearable
				|| item.information.getItemSettings().getParentSettings().wearable
		) && (item.information.getItemSettings().maxStackSize <= 1
				|| item.information.getItemSettings().getParentSettings().maxStackSize <= 1);
	}

	private boolean isDyeable(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
		return item.information.getItemSettings().dyeable || item.information.getItemSettings().getParentSettings().dyeable;
	}

	private Item registerWearableItem(RegistryHelperItemExpanded expanded, io.github.vampirestudios.obsidian.api.obsidian.item.Item item, ResourceLocation identifier,
									  Item.Properties settings, ResourceKey<CreativeModeTab> creativeTab) {
		if (item.information.getItemSettings().wearableSlot != null && !item.information.getItemSettings().wearableSlot.isEmpty()) {
			settings.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.byName(item.information.getItemSettings().wearableSlot)).build());
		} else if (item.information.getItemSettings().getParentSettings().wearableSlot != null && !item.information.getItemSettings().getParentSettings().wearableSlot.isEmpty()) {
			settings.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.byName(item.information.getItemSettings().getParentSettings().wearableSlot)).build());
		}

		if (isDyeable(item)) {
			int defaultDyeableColor = 0xFFFFFF;
			if (item.information.getItemSettings().defaultColor != 0) {
				defaultDyeableColor = item.information.getItemSettings().getDefaultColor();
			} else if (item.information.getItemSettings().getParentSettings().defaultColor != 0) {
				defaultDyeableColor = item.information.getItemSettings().getParentSettings().getDefaultColor();
			}
			settings.component(DataComponents.DYED_COLOR, new DyedItemColor(defaultDyeableColor));
		}
		return expanded.registerItem(identifier.getPath(), new ItemImpl(item, settings), creativeTab);
	}

	@Override
	public String getType() {
		return Utils.elementsDirPath(Registries.ITEM);
	}
}
