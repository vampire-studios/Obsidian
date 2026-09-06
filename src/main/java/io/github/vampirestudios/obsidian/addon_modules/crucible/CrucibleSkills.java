package io.github.vampirestudios.obsidian.addon_modules.crucible;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vampirestudios.obsidian.api.crucible.CrucibleSkill;
import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillEntry;
import io.github.vampirestudios.obsidian.api.crucible.SkillParser;
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
import java.util.*;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class CrucibleSkills implements AddonModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(CrucibleSkills.class);

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		if (!Objects.equals(id.format(), "crucible_like")) return;

		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();

		SkillParser.setModId(id.modId());
		try {
			Map<String, CrucibleSkill> items = mapper.readValue(AddonFormats.readAsJsonString(addon, file), new TypeReference<>() {
			});
			for (Map.Entry<String, CrucibleSkill> entry : items.entrySet()) {
				String skillName = entry.getKey();
				CrucibleSkill crucibleSkill = entry.getValue();
				if (crucibleSkill == null) {
					LOGGER.warn("Null crucible skill for key '{}' in file '{}'", skillName, file.getName());
					continue;
				}

				crucibleSkill.id = Identifier.fromNamespaceAndPath(id.modId(), skillName.toLowerCase(Locale.ROOT));

				List<Skill> steps = new ArrayList<>();
				for (String line : crucibleSkill.Skills) {
					if (line == null || line.isBlank()) continue;
					SkillEntry e = SkillParser.parseSkillString(line);
					if (e == null) continue;
					Skill s = SkillParser.createSkillFromEntry(e);
					if (s != null) steps.add(s);
				}

//				Skill wrapper = new SequenceSkill(crucibleSkill.id.toString(), new SelfTarget(), SkillTrigger.USE, steps);
//				SkillManager.getInstance().registerSkill(crucibleSkill.id, wrapper);

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
