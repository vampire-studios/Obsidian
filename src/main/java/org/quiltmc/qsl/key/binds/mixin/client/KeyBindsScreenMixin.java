/*
 * Copyright 2021-2022 QuiltMC
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

package org.quiltmc.qsl.key.binds.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(KeyBindsScreen.class)
public abstract class KeyBindsScreenMixin extends OptionsSubScreen {
	@Shadow
	private KeyBindsList controlsList;

	private KeyBindsScreenMixin(Screen screen, Options gameOptions, Component text) {
		super(screen, gameOptions, text);
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "render", at = @At("TAIL"))
	private void renderConflictTooltips(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		// TODO - Somehow extend the hover area to include the label too
//		ControlsListWidget.Entry entry = ((EntryListWidgetAccessor<ControlsListWidget.Entry>) this.keyBindList).invokeGetHoveredEntry();
//		if (entry != null && entry instanceof ControlsListWidget.KeyBindingEntry keyBindEntry) {
//			List<Text> tooltipLines = ((KeyBindTooltipHolder) keyBindEntry).getKeyBindTooltips();
//
//			if (tooltipLines != null) {
//				// TODO - With key names, it's getting too big! Add a maximum width
//				this.renderTooltip(matrices, tooltipLines, mouseX, mouseY);
//			}
//		}
	}
}