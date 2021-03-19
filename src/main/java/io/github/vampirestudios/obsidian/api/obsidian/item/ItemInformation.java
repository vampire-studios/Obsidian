package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ItemInformation {

	public String rarity = "common";
	public Identifier item_group;
	public Integer max_count = 64;
	public NameInformation name;
	public boolean has_glint = false;
	public boolean is_enchantable = false;
	public int enchantability = 5;
	public boolean hand_equipped = false;
	public int use_duration = 5;

	public Rarity getRarity() {
		switch (rarity) {
			case "uncommon":
				return Rarity.UNCOMMON;
			case "rare":
				return Rarity.RARE;
			case "epic":
				return Rarity.EPIC;
			case "common":
			default:
				return Rarity.COMMON;
		}
	}

	public ItemGroup getItemGroup() {
		return Obsidian.ITEM_GROUP_REGISTRY.get(item_group);
	}

}