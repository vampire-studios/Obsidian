package io.github.vampirestudios.obsidian.addon_modules.crucible;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vampirestudios.obsidian.api.crucible.CrucibleAugmentType;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class AugmentTypes implements AddonModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(AugmentTypes.class);

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		if (!Objects.equals(id.format(), "crucible_like")) return;

		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();

		try {
			Map<String, CrucibleAugmentType> types = mapper.readValue(AddonFormats.readAsJsonString(addon, file), new TypeReference<>() {
			});
			for (Map.Entry<String, CrucibleAugmentType> entry : types.entrySet()) {
				String typeName = entry.getKey();
				CrucibleAugmentType augmentType = entry.getValue();
				if (augmentType == null) {
					LOGGER.warn("Null augment type for key '{}' in file '{}'", typeName, file.getName());
					continue;
				}
				if (!augmentType.Enabled) continue;

				Identifier typeId = Identifier.fromNamespaceAndPath(id.modId(), typeName.toLowerCase(Locale.ROOT));
				register(ContentRegistries.AUGMENT_TYPES, "augment_type", typeId, augmentType);
			}
		} catch (Exception e) {
			failedRegistering("augment_type", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "augment_types";
	}
}
