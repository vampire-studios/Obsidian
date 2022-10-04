package io.github.vampirestudios.obsidian.mixins.client;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.option.KeyBindListWidget;
import net.minecraft.client.option.KeyBind;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindListWidget.KeyBindEntry.class)
public class KeyBindListWidget$KeyBindEntryMixin {

	@Mutable
	@Shadow
	public ButtonWidget bindButton;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onInit(KeyBindListWidget keyBindListWidget, KeyBind keyBind, Text text, CallbackInfo ci) {
		KeyBindListWidget widget = (KeyBindListWidget) (Object) this;
		this.bindButton = new ButtonWidget(0, 0, 75 + 20, 20, text, button -> widget.parent.focusedKey = keyBind) {
			@Override
			protected MutableText getNarrationMessage() {
				return keyBind.isUnbound()
						? Text.translatable("narrator.controls.unbound", text)
						: Text.translatable("narrator.controls.bound", text, super.getNarrationMessage());
			}
		};
	}

}
