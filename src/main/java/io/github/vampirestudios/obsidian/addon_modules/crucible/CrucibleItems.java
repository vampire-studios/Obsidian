package io.github.vampirestudios.obsidian.addon_modules.crucible;

import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.api.crucible.*;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.minecraft.crucible.ItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class CrucibleItems implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError, DeserializationException {
		if (!Objects.equals(id.format(), "crucible_like")) return;

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();
		try {
			Map<String, CrucibleItem> items = mapper.readValue(file, new TypeReference<>() {});
			for (Map.Entry<String, CrucibleItem> entry : items.entrySet()) {
				String itemName = entry.getKey();
				CrucibleItem crucibleItem = entry.getValue();
				if (crucibleItem == null) return;

				SkillManager skillManager = SkillManager.getInstance();
				SkillParser.setModId(id.modId());

				if (crucibleItem.Skills != null) {
					for (String skillString : crucibleItem.Skills) {
						SkillEntry skillEntry = SkillParser.parseSkillString(skillString);
						if (skillEntry != null) {
							Skill skill = SkillParser.createSkillFromEntry(skillEntry);
							if (skill != null) {
								crucibleItem.internalSkills = new ArrayList<>();
								crucibleItem.internalSkills.add(skill);
								skillManager.registerSkill(skill);
							} else {
								System.err.println(STR."Failed to create Skill from SkillEntry for skillString: \{skillString}");
							}
						} else {
							System.err.println(STR."Failed to parse skillString: \{skillString}");
						}
					}

					crucibleItem.Skills.forEach(s -> System.out.println("AAA: " + s));
				}

				PlayerBlockBreakEvents.AFTER.register((_, player, pos, _, _) ->
						skillManager.triggerSkills(SkillTrigger.ON_BLOCK_BREAK, new SkillContext(player, null, pos))
				);

				crucibleItem.id = ResourceLocation.fromNamespaceAndPath(id.modId(), itemName.toLowerCase(Locale.ROOT));

				RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());

				Item.Properties itemProperties = new Item.Properties();
				itemProperties.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, crucibleItem.id));

				Item item = expanded.registerItem(itemName, new ItemImpl(crucibleItem, skillManager, itemProperties));
				ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> entries.accept(item));

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
}
