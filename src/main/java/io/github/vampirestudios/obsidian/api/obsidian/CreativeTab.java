package io.github.vampirestudios.obsidian.api.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.util.HolderSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;

import java.util.Optional;

public class CreativeTab {
    public static final Codec<CreativeTab> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("texture").forGetter(creativeTab -> creativeTab.texture),
            Registry.ITEM.getCodec().fieldOf("icon").forGetter(creativeTab -> creativeTab.icon),
            RegistryCodecs.homogeneousList(Registry.ITEM_KEY).fieldOf("items").forGetter(creativeTab -> creativeTab.items),
            Codec.BOOL.fieldOf("no_scroll_bar").forGetter(creativeTab -> creativeTab.noScrollBar)
    ).apply(instance, CreativeTab::new));

    public Optional<Identifier> texture;
    public Item icon;
    public HolderSet<Item> items;
    public boolean noScrollBar;

    public CreativeTab(Optional<Identifier> texture, Item icon, HolderSet<Item> items, boolean noScrollBar) {
        this.texture = texture;
        this.icon = icon;
        this.items = items;
        this.noScrollBar = noScrollBar;
    }
}
