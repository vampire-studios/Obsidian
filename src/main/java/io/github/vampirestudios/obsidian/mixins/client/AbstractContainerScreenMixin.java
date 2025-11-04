package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.minecraft.ObsidianBundleMouseActions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

	protected AbstractContainerScreenMixin(Component component) {
		super(component);
	}

	@Shadow protected abstract void addItemSlotMouseAction(ItemSlotMouseAction itemSlotMouseAction);

	@Inject(method = "init", at = @At(value = "TAIL"))
	public void onInit(CallbackInfo ci) {
		this.addItemSlotMouseAction(new ObsidianBundleMouseActions(this.minecraft));
	}
}
