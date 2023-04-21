package io.github.vampirestudios.obsidian.api.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CreativeTab {
    public static final Codec<CreativeTab> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(creativeTab -> creativeTab.texture),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("icon").forGetter(creativeTab -> creativeTab.icon),
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("items").forGetter(creativeTab -> creativeTab.items),
            Codec.BOOL.fieldOf("no_scroll_bar").forGetter(creativeTab -> creativeTab.noScrollBar)
    ).apply(instance, CreativeTab::new));

    public Optional<ResourceLocation> texture;
    public Item icon;
    public HolderSet<Item> items;
    public boolean noScrollBar;

    public CreativeTab(Optional<ResourceLocation> texture, Item icon, HolderSet<Item> items, boolean noScrollBar) {
        this.texture = texture;
        this.icon = icon;
        this.items = items;
        this.noScrollBar = noScrollBar;
    }
}
