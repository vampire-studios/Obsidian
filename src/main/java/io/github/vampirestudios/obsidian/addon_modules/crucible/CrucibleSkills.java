package io.github.vampirestudios.obsidian.addon_modules.crucible;

import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.api.crucible.*;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class CrucibleSkills implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError, DeserializationException {
		if (!Objects.equals(id.format(), "crucible_like")) return;

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();
		try {
			Map<String, CrucibleSkill> items = mapper.readValue(file, new TypeReference<>() {});
			for (Map.Entry<String, CrucibleSkill> entry : items.entrySet()) {
				String skillName = entry.getKey();
				CrucibleSkill crucibleSkill = entry.getValue();
				if (crucibleSkill == null) return;

				SkillManager skillManager = SkillManager.getInstance();
				SkillParser.setModId(id.modId());

				// Inside your loop where you parse and create skills
				for (String skillString : crucibleSkill.Skills) {
					SkillEntry skillEntry = SkillParser.parseSkillString(skillString);
					if (skillEntry != null) {
						Skill skill = SkillParser.createSkillFromEntry(skillEntry);
						if (skill != null) {
							crucibleSkill.internalSkills.add(skill);
							skillManager.registerSkill(ResourceLocation.fromNamespaceAndPath(id.modId(), skillName.toLowerCase(Locale.ROOT)), skill);
						} else {
							System.err.println(STR."Failed to create Skill from SkillEntry for skillString: \{skillString}");
						}
					} else {
						System.err.println(STR."Failed to parse skillString: \{skillString}");
					}
				}

				crucibleSkill.id = ResourceLocation.fromNamespaceAndPath(id.modId(), skillName.toLowerCase(Locale.ROOT));

				register(ContentRegistries.CRUCIBLE_SKILLS, "crucible_skill", crucibleSkill.id, crucibleSkill);
			}
		} catch (Exception e) {
			failedRegistering("crucible_skill", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "skills";
	}
}
