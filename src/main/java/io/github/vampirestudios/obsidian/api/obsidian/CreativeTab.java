package io.github.vampirestudios.obsidian.api.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class CreativeTab {
    public static final Codec<CreativeTab> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("texture").forGetter(creativeTab -> creativeTab.texture),
            Registries.ITEM.getCodec().fieldOf("icon").forGetter(creativeTab -> creativeTab.icon),
            RegistryCodecs.entryList(RegistryKeys.ITEM).fieldOf("items").forGetter(creativeTab -> creativeTab.items),
            Codec.BOOL.fieldOf("no_scroll_bar").forGetter(creativeTab -> creativeTab.noScrollBar)
    ).apply(instance, CreativeTab::new));

    public Optional<Identifier> texture;
    public Item icon;
    public RegistryEntryList<Item> items;
    public boolean noScrollBar;

    public CreativeTab(Optional<Identifier> texture, Item icon, RegistryEntryList<Item> items, boolean noScrollBar) {
        this.texture = texture;
        this.icon = icon;
        this.items = items;
        this.noScrollBar = noScrollBar;
    }
}
