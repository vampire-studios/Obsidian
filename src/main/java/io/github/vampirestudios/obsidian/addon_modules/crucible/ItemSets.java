package io.github.vampirestudios.obsidian.addon_modules.crucible;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vampirestudios.obsidian.api.crucible.*;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class ItemSets implements AddonModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemSets.class);

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		if (!Objects.equals(id.format(), "crucible_like")) return;

		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();

		SkillParser.setModId(id.modId());
		try {
			Map<String, CrucibleItemSet> sets = mapper.readValue(AddonFormats.readAsJsonString(addon, file), new TypeReference<>() {
			});
			for (Map.Entry<String, CrucibleItemSet> entry : sets.entrySet()) {
				String setName = entry.getKey();
				CrucibleItemSet itemSet = entry.getValue();
				if (itemSet == null) {
					LOGGER.warn("Null item set for key '{}' in file '{}'", setName, file.getName());
					continue;
				}

				// Parse skills in each tier bonus
				if (itemSet.Bonuses != null) {
					for (ItemSetBonus bonus : itemSet.Bonuses.values()) {
						if (bonus.Skills == null) continue;
						bonus.internalSkills = new ArrayList<>();
						for (String skillString : bonus.Skills) {
							SkillEntry entry2 = SkillParser.parseSkillString(skillString);
							if (entry2 == null) continue;
							Skill skill = SkillParser.createSkillFromEntry(entry2);
							if (skill != null) bonus.internalSkills.add(skill);
						}
					}
				}

				Identifier setId = Identifier.fromNamespaceAndPath(id.modId(), setName.toLowerCase(Locale.ROOT));
				register(ContentRegistries.ITEM_SETS, "item_set", setId, itemSet);
			}
		} catch (Exception e) {
			failedRegistering("item_set", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item_sets";
	}
}
