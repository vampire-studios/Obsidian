package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.IForgeItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreativeModeTab.class)
public class ItemGroupMixin implements IForgeItemGroup {
	ResourceLocation backgroundLocation;

	@Override
	public CreativeModeTab setBackgroundImage(ResourceLocation texture) {
		this.backgroundLocation = texture;
		return (CreativeModeTab) (Object) this;
	}

	@Override
	public ResourceLocation getBackgroundImage() {
		if (backgroundLocation != null) return backgroundLocation; //FORGE: allow custom namespace
		return IForgeItemGroup.super.getBackgroundImage();
	}
}
