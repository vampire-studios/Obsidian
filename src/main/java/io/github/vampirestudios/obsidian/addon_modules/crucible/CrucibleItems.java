package io.github.vampirestudios.obsidian.addon_modules.crucible;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vampirestudios.obsidian.api.crucible.*;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.minecraft.crucible.ItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class CrucibleItems implements AddonModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(CrucibleItems.class);

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		if (!Objects.equals(id.format(), "crucible_like")) return;

		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		try {
			Map<String, CrucibleItem> items = mapper.readValue(AddonFormats.readAsJsonString(addon, file), new TypeReference<>() {
			});
			for (Map.Entry<String, CrucibleItem> entry : items.entrySet()) {
				String itemName = entry.getKey();
				CrucibleItem crucibleItem = entry.getValue();
				if (crucibleItem == null) {
					LOGGER.warn("Null crucible item for key '{}' in file '{}'", itemName, file.getName());
					continue;
				}

				SkillParser.setModId(id.modId());

				if (crucibleItem.Skills != null) {
					crucibleItem.internalSkills = new ArrayList<>();

					for (String skillString : crucibleItem.Skills) {
						SkillEntry skillEntry = SkillParser.parseSkillString(skillString);
						if (skillEntry == null) continue;

						Skill skill = SkillParser.createSkillFromEntry(skillEntry);
						if (skill == null) continue;

						crucibleItem.internalSkills.add(skill);
					}
				}

				crucibleItem.id = Identifier.fromNamespaceAndPath(id.modId(), itemName.toLowerCase(Locale.ROOT));

				// Register augment if this item has an Augmentation section
				if (crucibleItem.Augmentation != null) {
					CrucibleItem.AugmentationDef aug = crucibleItem.Augmentation;
					if (aug.Skills != null) {
						aug.internalSkills = new ArrayList<>();
						for (String skillString : aug.Skills) {
							SkillEntry skillEntry = SkillParser.parseSkillString(skillString);
							if (skillEntry == null) continue;
							Skill skill = SkillParser.createSkillFromEntry(skillEntry);
							if (skill != null) aug.internalSkills.add(skill);
						}
					}
					CrucibleAugment augment = new CrucibleAugment();
					augment.id = crucibleItem.id;
					augment.displayName = crucibleItem.Display;
					augment.Type = aug.Type;
					augment.Tooltip = aug.Tooltip;
					augment.Icon = aug.Icon;
					augment.Conditions = aug.Conditions;
					augment.Attributes = aug.Attributes;
					augment.internalSkills = aug.internalSkills;
					register(ContentRegistries.AUGMENTS, "augment", crucibleItem.id, augment);
				}

				RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());

				Item.Properties itemProperties = new Item.Properties();
				itemProperties.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, crucibleItem.id));

				// Initialize AugmentSocketData if this item has augment slots defined
				if (crucibleItem.AugmentSlots != null && !crucibleItem.AugmentSlots.isEmpty()) {
					List<AugmentSlotEntry> slotEntries = new ArrayList<>();
					Map<String, Integer> maxSlotsPerType = new HashMap<>();
					Random rng = new Random();
					for (CrucibleItem.AugmentSlotDef slotDef : crucibleItem.AugmentSlots) {
						if (slotDef.Chance < 1.0 && rng.nextDouble() >= slotDef.Chance) continue;
						int amount = rollAmount(slotDef.Amount, rng);
						String typeName = slotDef.Type != null ? slotDef.Type.toUpperCase(Locale.ROOT) : "GENERIC";
						for (int i = 0; i < amount; i++) {
							slotEntries.add(new AugmentSlotEntry(typeName, Optional.empty()));
						}
						if (slotDef.MaxAmount != Integer.MAX_VALUE) {
							maxSlotsPerType.merge(typeName, slotDef.MaxAmount, Math::min);
						}
					}
					AugmentSocketData defaultSockets = new AugmentSocketData(
							Collections.unmodifiableList(slotEntries), Map.copyOf(maxSlotsPerType));
					itemProperties.component(OItemComponents.AUGMENT_SOCKETS, defaultSockets);
				}

				Item item = expanded.registerItem(itemName, new ItemImpl(crucibleItem, itemProperties));
				CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> entries.accept(item));

				register(ContentRegistries.CRUCIBLE_ITEMS, "crucible_item", crucibleItem.id, crucibleItem);
			}
		} catch (Exception e) {
			failedRegistering("crucible_item", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "items";
	}

	/** Parses "N" or "XtoY" slot amount strings and rolls a value. */
	private static int rollAmount(String amountStr, Random rng) {
		if (amountStr == null || amountStr.isBlank()) return 1;
		String s = amountStr.trim();
		int toIdx = s.toLowerCase(Locale.ROOT).indexOf("to");
		if (toIdx > 0) {
			try {
				int min = Integer.parseInt(s.substring(0, toIdx).trim());
				int max = Integer.parseInt(s.substring(toIdx + 2).trim());
				return min + rng.nextInt(Math.max(1, max - min + 1));
			} catch (NumberFormatException ignored) {
			}
		}
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ignored) {
		}
		return 1;
	}
}
