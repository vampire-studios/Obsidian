//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.vampirestudios.obsidian;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class ObsidianPackSettings {
    public static final ObsidianPackSettings SAFE_MODE = new ObsidianPackSettings(ImmutableList.of("vanilla"), ImmutableList.of());
    public static final Codec<ObsidianPackSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.STRING.listOf().fieldOf("Enabled").forGetter((obsidianPackSettings) -> {
            return obsidianPackSettings.enabled;
        }), Codec.STRING.listOf().fieldOf("Disabled").forGetter((obsidianPackSettings) -> {
            return obsidianPackSettings.disabled;
        })).apply(instance, ObsidianPackSettings::new);
    });
    private final List<String> enabled;
    private final List<String> disabled;

    public ObsidianPackSettings(List<String> enabled, List<String> disabled) {
        this.enabled = ImmutableList.copyOf(enabled);
        this.disabled = ImmutableList.copyOf(disabled);
    }

    public List<String> getEnabled() {
        return this.enabled;
    }

    public List<String> getDisabled() {
        return this.disabled;
    }
}
