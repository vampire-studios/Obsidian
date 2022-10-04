package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.IForgeItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemGroup.class)
public class ItemGroupMixin implements IForgeItemGroup {
	Identifier backgroundLocation;

	@Override
	public ItemGroup setBackgroundImage(Identifier texture) {
		this.backgroundLocation = texture;
		return (ItemGroup) (Object) this;
	}

	@Override
	public Identifier getBackgroundImage() {
		if (backgroundLocation != null) return backgroundLocation; //FORGE: allow custom namespace
		return IForgeItemGroup.super.getBackgroundImage();
	}
}
