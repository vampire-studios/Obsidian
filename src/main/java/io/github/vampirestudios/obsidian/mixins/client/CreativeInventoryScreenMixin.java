package io.github.vampirestudios.obsidian.mixins.client;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin {
	@Shadow private static int selectedTab;

	@ModifyConstant(method = "drawForeground", constant = @Constant(intValue = 4210752))
	public int readNbt(int old) {
		ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
		return itemGroup.getLabelColor();
	}
}