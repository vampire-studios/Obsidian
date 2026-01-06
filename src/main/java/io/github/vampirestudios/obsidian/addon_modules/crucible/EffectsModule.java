package io.github.vampirestudios.obsidian.addon_modules.crucible;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.EffectFactory;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.EffectType;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class EffectsModule implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
        if (!Objects.equals(id.format(), "crucible_like")) return;

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();

        try {
            // Parse effects.yml file into a map of effect definitions
            Map<String, Map<String, Object>> effectsConfig = mapper.readValue(file, new TypeReference<>() {});

            // Register each effect
            for (Map.Entry<String, Map<String, Object>> entry : effectsConfig.entrySet()) {
                String effectName = entry.getKey();
                Map<String, Object> effectData = entry.getValue();

                // Determine the type of effect
                String typeString = (String) effectData.get("type");
                EffectType effectType = EffectType.fromString(typeString);

                // Use the factory to create the effect
                Effect effect = EffectFactory.createEffect(effectType, effectData);
                if (effect == null) {
                    System.err.println("Failed to create effect: " + effectName);
                    continue;
                }

                // Set the Identifier for the effect
                Identifier effectId = Identifier.fromNamespaceAndPath(id.modId(), effectName.toLowerCase(Locale.ROOT));
                effect.setId(effectId);

                // Register the effect in the registry
                register(ContentRegistries.CRUCIBLE_EFFECTS, "crucible_effect", effectId, effect);
            }
        } catch (Exception e) {
            failedRegistering("crucible_effect", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "effects";
    }
}
