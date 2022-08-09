package io.github.vampirestudios.obsidian.configPack;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.Processor;
import org.quiltmc.config.api.values.ValueList;

@Processor("setFormat")
class MyConfig extends WrappedConfig {
	public final Modules modules = new Modules();

	public void setSerializer(Config.Builder builder) {
		builder.format("yaml");
	}

	public class Modules implements Section {
		@Comment("List of enabled modules.")
		public final ValueList<String> enabled = ValueList.create("");
	}
}