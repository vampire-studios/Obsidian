package io.github.vampirestudios.obsidian.mixins.client.emoji;

import io.github.vampirestudios.obsidian.api.EmojiCode;
import io.github.vampirestudios.obsidian.api.EmojiType;
import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(SelectionManager.class)
public abstract class SelectionManagerMixin {

	@Shadow
	private int selectionStart;

	@Shadow
	@Final
	private Supplier<String> stringGetter;

	@Shadow
	public abstract void delete(int cursorOffset);

	@Shadow
	public abstract void insert(String string);

	@Inject(method = "insert(Ljava/lang/String;Ljava/lang/String;)V", at = @At("TAIL"))
	private void inject(String _unused, String insertion, CallbackInfo ci) {
		String text = stringGetter.get();
		for (EmojiCode ec : EmojiType.emojiCodes) {
			if (ec.match(text, selectionStart - 1)) {
				delete(-ec.getCode().length());
				insert(ec.getEmoji());
				break;
			}
		}
	}
}