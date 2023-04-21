/*
 * Copyright 2022 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.key.binds.mixin.client.chords;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2BooleanAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.key.binds.impl.chords.KeyChord;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.SortedMap;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;

@Mixin(KeyBindsScreen.class)
public abstract class KeyBindsScreenMixin extends OptionsSubScreen {
	@Shadow
	@Nullable
	public KeyMapping selectedKeyBinding;

	@Shadow
	public long lastKeyCodeUpdateTime;

	@Unique
	private List<InputConstants.Key> quilt$focusedProtoChord;

	@Unique
	private boolean quilt$initialMouseRelease;

	public KeyBindsScreenMixin(Screen screen, Options gameOptions, Component text) {
		super(screen, gameOptions, text);
	}

	@Inject(at = @At("TAIL"), method = "init")
	private void initializeProtoChord(CallbackInfo ci) {
		this.quilt$focusedProtoChord = new ObjectArrayList<>();
		this.quilt$initialMouseRelease = true;
	}

	@Inject(
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/option/GameOptions;setKeyCode(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/util/InputUtil$Key;)V"
			),
			method = "mouseClicked",
			cancellable = true
	)
	private void modifyMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(button);
		if (!quilt$focusedProtoChord.contains(key)) {
			quilt$focusedProtoChord.add(key);
		}

		cir.setReturnValue(true);
	}

	@Inject(at = @At(value = "RETURN", ordinal = 1), method = "mouseClicked")
	private void excludeFirstMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		this.quilt$initialMouseRelease = true;
	}

	@Inject(
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/option/GameOptions;setKeyCode(Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/client/util/InputUtil$Key;)V",
				ordinal = 1
			),
			method = "keyPressed",
			cancellable = true
	)
	private void modifyKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
		if (!quilt$focusedProtoChord.contains(key)) {
			quilt$focusedProtoChord.add(key);
		}

		cir.setReturnValue(true);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (this.selectedKeyBinding != null) {
			if (quilt$focusedProtoChord.size() == 1) {
				this.options.setKey(this.selectedKeyBinding, quilt$focusedProtoChord.get(0));
			} else if (quilt$focusedProtoChord.size() > 1) {
				SortedMap<InputConstants.Key, Boolean> map = new Object2BooleanAVLTreeMap<>();
				for (InputConstants.Key key : quilt$focusedProtoChord) {
					map.put(key, false);
				}

				this.selectedKeyBinding.setBoundChord(new KeyChord(map));
//				QuiltKeyBindsConfigManager.updateConfig(false);
			}

			quilt$focusedProtoChord.clear();
			this.selectedKeyBinding = null;
			this.lastKeyCodeUpdateTime = Util.getMillis();
			KeyMapping.resetMapping();
			return true;
		} else {
			return super.keyReleased(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		// TODO - Don't duplicate code, have a common method
		if (this.selectedKeyBinding != null && !this.quilt$initialMouseRelease) {
			if (quilt$focusedProtoChord.size() == 1) {
				this.options.setKey(this.selectedKeyBinding, quilt$focusedProtoChord.get(0));
			} else if (quilt$focusedProtoChord.size() > 1) {
				SortedMap<InputConstants.Key, Boolean> map = new Object2BooleanAVLTreeMap<>();
				for (InputConstants.Key key : quilt$focusedProtoChord) {
					map.put(key, false);
				}

				this.selectedKeyBinding.setBoundChord(new KeyChord(map));
//				QuiltKeyBindsConfigManager.updateConfig(false);
			}

			quilt$focusedProtoChord.clear();
			this.selectedKeyBinding = null;
			this.lastKeyCodeUpdateTime = Util.getMillis();
			KeyMapping.resetMapping();
			return true;
		} else {
			this.quilt$initialMouseRelease = false;
			return super.mouseReleased(mouseX, mouseY, button);
		}
	}
}