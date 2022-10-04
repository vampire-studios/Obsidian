package io.github.vampirestudios.obsidian.api;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public interface IForgeItemGroup {

	private ItemGroup self() {
		return (ItemGroup) this;
	}

	default ItemGroup setBackgroundImage(Identifier texture) {
		return self();
	}

	default Identifier getBackgroundImage() {
		return new Identifier("textures/gui/container/creative_inventory/tab_" + self().getTexture());
	}

	Identifier CREATIVE_INVENTORY_TABS = new Identifier("textures/gui/container/creative_inventory/tabs.png");

	default Identifier getTabsImage() {
		return CREATIVE_INVENTORY_TABS;
	}

	default int getLabelColor() {
		return 4210752;
	}

}
