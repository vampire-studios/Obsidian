package io.github.vampirestudios.obsidian.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public interface IForgeItemGroup {

	private CreativeModeTab self() {
		return (CreativeModeTab) this;
	}

	default CreativeModeTab setBackgroundImage(ResourceLocation texture) {
		return self();
	}

	default ResourceLocation getBackgroundImage() {
		return new ResourceLocation("textures/gui/container/creative_inventory/tab_" + self().getBackgroundSuffix());
	}

	ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");

	default ResourceLocation getTabsImage() {
		return CREATIVE_INVENTORY_TABS;
	}

	default int getLabelColor() {
		return 4210752;
	}

}
