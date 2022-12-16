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

package org.quiltmc.qsl.key.binds.mixin.client;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.objectweb.asm.Opcodes;
import org.quiltmc.qsl.key.binds.impl.KeyBindRegistryImpl;
import org.quiltmc.qsl.key.binds.impl.KeyBindTooltipHolder;
import org.quiltmc.qsl.key.binds.impl.chords.KeyChord;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ControlsListWidget.KeyBindingEntry.class)
public abstract class KeyBindEntryMixin extends ControlsListWidget.Entry implements KeyBindTooltipHolder {
	@Shadow
	@Final
	private KeyBinding binding;

	@Shadow
	@Final
	private ButtonWidget editButton;

	@Unique
	private List<Text> quilt$conflictTooltips = new ObjectArrayList<>();

	@Unique
	private List<InputUtil.Key> quilt$previousProtoChord;

	@Unique
	private static List<InputUtil.Key> quilt$changedProtoChord;

	@Unique
	private boolean quilt$addKeyNameToTooltip;

	@Shadow(aliases = "field_2742", remap = false)
	@Final
	ControlsListWidget field_2742;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initPreviousBoundKey(ControlsListWidget list, KeyBinding key, Text text, CallbackInfo ci) {
		quilt$previousProtoChord = null;
		quilt$changedProtoChord = null;
		quilt$addKeyNameToTooltip = false;
	}

	@Inject(
			method = "render",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/option/KeyBinding;isUnbound()Z"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void collectConflictTooltips(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci, boolean bl, boolean bl2) {
		InputUtil.Key boundKey = this.binding.getBoundKey();
		KeyChord boundChord = this.binding.getBoundChord();
		List<InputUtil.Key> boundProtoChord;

		if (boundChord == null) {
			boundProtoChord = List.of(boundKey);
		} else {
			boundProtoChord = List.copyOf(boundChord.keys.keySet());
		}

		if (!boundProtoChord.equals(this.quilt$previousProtoChord) || quilt$changedProtoChord != null) {
			this.quilt$conflictTooltips.clear();
			if (quilt$changedProtoChord != null && quilt$changedProtoChord.equals(boundProtoChord)) {
				quilt$changedProtoChord = null;
			} else {
				quilt$changedProtoChord = boundProtoChord;
			}

			quilt$addKeyNameToTooltip = true;

			if (!this.binding.isUnbound()) {
				for (KeyBinding otherKey : KeyBindRegistryImpl.getKeyBinds()) {
					if (otherKey != this.binding && this.binding.equals(otherKey)) {
						if (this.quilt$conflictTooltips.isEmpty()) {
							this.quilt$conflictTooltips.add(Text.translatable("key.qsl.key_conflict.tooltip").formatted(Formatting.RED));
						}

						this.quilt$conflictTooltips.add(Text.translatable("key.qsl.key_conflict.tooltip.entry", Text.translatable(otherKey.getTranslationKey())).formatted(Formatting.RED));
					}
				}
			}
		}

		this.quilt$previousProtoChord = boundProtoChord;
	}

	@Inject(
			method = "render",
			at = @At(
				value = "JUMP",
				opcode = Opcodes.IFEQ,
				ordinal = 1,
				shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void shortenText(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
		// TODO - Get client from the parent screen instead
		MinecraftClient client = MinecraftClient.getInstance();
		Text text = this.editButton.getMessage();
		int targetWidth = /*bl || bl2 ? 50 - 10 : */75 - 10;
		if (client.textRenderer.getWidth(text) > targetWidth) {
			StringBuilder protoText = new StringBuilder(text.getString());
			if (this.binding.getBoundChord() != null) {
				protoText = new StringBuilder();
				KeyChord chord = this.binding.getBoundChord();

				for (InputUtil.Key key : chord.keys.keySet()) {
					if (protoText.length() > 0) {
						protoText.append(" + ");
					}

					StringBuilder keyString = new StringBuilder(key.getLocalizedText().getString());

					if (keyString.length() > 3) {
						String[] keySegments = keyString.toString().split(" ");
						keyString = new StringBuilder();
						for (String keySegment : keySegments) {
							keyString.append(keySegment.charAt(0));
						}
					}

					protoText.append(keyString);
				}
			}

			if (client.textRenderer.getWidth(protoText.toString()) > targetWidth) {
				if (quilt$addKeyNameToTooltip) {
					this.quilt$conflictTooltips.add(0, this.binding.getBoundKeyLocalizedText());
					quilt$addKeyNameToTooltip = false;
				}

				protoText = new StringBuilder(client.textRenderer.trimToWidth(protoText.toString(), targetWidth));
				protoText.append("...");
			}

			this.editButton.setMessage(Text.literal(protoText.toString()));
		}
	}

	@ModifyArg(
			method = "render",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/gui/widget/ButtonWidget;setMessage(Lnet/minecraft/text/Text;)V",
				ordinal = 2
			)
	)
	private Text addConflictIndicator(Text originalText) {
		return Text.translatable("key.qsl.key_conflict.indicator", originalText).formatted(Formatting.RED);
	}

	@Override
	public List<Text> getKeyBindTooltips() {
		return this.quilt$conflictTooltips;
	}
}