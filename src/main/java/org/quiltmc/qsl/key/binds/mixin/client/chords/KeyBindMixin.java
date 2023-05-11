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
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.quiltmc.qsl.key.binds.api.ChordedKeyBind;
import org.quiltmc.qsl.key.binds.impl.chords.KeyChord;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

@Mixin(KeyMapping.class)
public class KeyBindMixin implements ChordedKeyBind {
	@Shadow
	@Final
	private static Map<String, KeyMapping> ALL;

	@Shadow
	private InputConstants.Key key;

	@Unique
	private static final Map<KeyChord, KeyMapping> KEY_BINDS_BY_CHORD = new Reference2ReferenceOpenHashMap<>();

	@Unique
	private KeyChord quilt$defaultChord;

	@Unique
	private KeyChord quilt$boundChord;

	@Inject(
			at = @At("RETURN"),
			method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V"
	)
	private void initializeChordFields(String string, InputConstants.Type type, int i, String string2, CallbackInfo ci) {
		quilt$defaultChord = null;
		quilt$boundChord = null;
	}

	@Inject(at = @At("HEAD"), method = "click")
	private static void detectChordsOnIncrement(InputConstants.Key startingKey, CallbackInfo ci) {
		for (KeyChord chord : KEY_BINDS_BY_CHORD.keySet()) {
			if (chord.keys.containsKey(startingKey) && !chord.keys.containsValue(false)) {
				// This ensures that the chord will only be incremented once instead of N times
				if (startingKey.equals(chord.keys.keySet().toArray()[0])) {
					KeyMapping keyBind = KEY_BINDS_BY_CHORD.get(chord);
					((KeyBindAccessor) keyBind).setClickCount(((KeyBindAccessor) keyBind).getClickCount() + 1);
				}
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "set")
	private static void detectChordsOnSet(InputConstants.Key startingKey, boolean pressed, CallbackInfo ci) {
		for (KeyChord chord : KEY_BINDS_BY_CHORD.keySet()) {
			if (chord.keys.containsKey(startingKey)) {
				chord.keys.put(startingKey, pressed);
				KEY_BINDS_BY_CHORD.get(chord).setDown(!chord.keys.containsValue(false));
			}
		}
	}

	@Inject(
			at = @At(
				value = "INVOKE",
				target = "Lcom/mojang/blaze3d/platform/InputConstants$Key;getType()Lcom/mojang/blaze3d/platform/InputConstants$Type;"
			),
			method = "setAll",
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private static void updateChordsToo(CallbackInfo ci, Iterator<?> iterator, KeyMapping keyBind) {
		KeyChord chord = ((KeyBindMixin) (Object) keyBind).quilt$boundChord;
		if (chord != null) {
			long window = Minecraft.getInstance().getWindow().getWindow();
			for (InputConstants.Key key : chord.keys.keySet()) {
				if (key.getType() == InputConstants.Type.KEYSYM) {
					chord.keys.put(key, InputConstants.isKeyDown(window, key.getValue()));
				}
			}
			KEY_BINDS_BY_CHORD.get(chord).setDown(!chord.keys.containsValue(false));
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "setAll")
	private static void updateChordBoundKeys(CallbackInfo cir) {
		KEY_BINDS_BY_CHORD.clear();

		for (KeyMapping key : ALL.values()) {
			KeyChord chord = ((KeyBindMixin) (Object) key).quilt$boundChord;
			if (chord != null) {
				KEY_BINDS_BY_CHORD.put(chord, key);
			}
		}
	}

	// TODO - Detect chords for matchesKey too; They are such a weird case
	@Inject(at = @At("HEAD"), method = "matches", cancellable = true)
	private void matchesChordKey(CallbackInfoReturnable<Boolean> ci) { }

	// TODO - Detect chords for matchesMouseButton as well

	@Inject(at = @At("HEAD"), method = "setKey", cancellable = true)
	private void resetChord(CallbackInfo ci) {
		this.quilt$boundChord = null;
	}

	@Inject(at = @At("HEAD"), method = "same", cancellable = true)
	private void keyOrChordEquals(KeyMapping other, CallbackInfoReturnable<Boolean> cir) {
		if (this.quilt$boundChord != null) {
			if (other.getBoundChord() != null) {
				cir.setReturnValue(this.quilt$boundChord.equals(other.getBoundChord()));
			} else {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "isUnbound", cancellable = true)
	private void isChordUnbound(CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ() && this.quilt$boundChord != null) {
			cir.setReturnValue(false);
		}
	}

	@Inject(at = @At("HEAD"), method = "getTranslatedKeyMessage", cancellable = true)
	private void useChordName(CallbackInfoReturnable<Component> cir) {
		if (this.quilt$boundChord != null) {
			MutableComponent text = Component.empty();
			for (InputConstants.Key key : this.quilt$boundChord.keys.keySet()) {
				if (text.getSiblings().size() != 0) {
					text.append(" + ");
				}

				text.append(key.getDisplayName());
			}

			cir.setReturnValue(text);
		}
	}

	@Inject(at = @At("HEAD"), method = "isDefault", cancellable = true)
	private void detectDefaultChord(CallbackInfoReturnable<Boolean> cir) {
		if (this.quilt$boundChord != null) {
			cir.setReturnValue(this.quilt$boundChord.equals(this.quilt$defaultChord));
		}
	}

	@Override
	public KeyChord getBoundChord() {
		return this.quilt$boundChord;
	}

	@Override
	public void setBoundChord(KeyChord chord) {
		this.key = InputConstants.UNKNOWN;
		this.quilt$boundChord = chord;
	}

	@Override
	public KeyMapping withChord(InputConstants.Key... keys) {
		// TODO - Perhaps have cases for length 0 and 1?
		if (keys.length > 1) {
			SortedMap<InputConstants.Key, Boolean> protoChord = new Object2BooleanAVLTreeMap<>();
			for (InputConstants.Key key : keys) {
				protoChord.put(key, false);
			}

			KeyChord chord = new KeyChord(protoChord);
			this.setBoundChord(chord);
			this.quilt$defaultChord = chord;
			KeyMapping.resetMapping();
		}

		return (KeyMapping) (Object) this;
	}
}