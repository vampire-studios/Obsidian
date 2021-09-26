package io.github.vampirestudios.obsidian.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "obsidian_config")
public class ObsidianConfig implements ConfigData {

    public String addonsFolder = "obsidian_addons";

}
