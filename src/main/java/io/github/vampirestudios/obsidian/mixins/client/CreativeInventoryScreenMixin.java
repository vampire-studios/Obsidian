package io.github.vampirestudios.obsidian.mixins.client;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin {
	@Shadow private static CreativeModeTab selectedTab;

	@ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 4210752))
	public int readNbt(int old) {
		CreativeModeTab itemGroup = selectedTab;
		return itemGroup.getLabelColor();
	}
}