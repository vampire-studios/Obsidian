package io.github.vampirestudios.obsidian.mixins;

import com.glisco.owo.moddata.ModDataConsumer;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModDataConsumer.class)
public interface GroupTabLoaderAccessor {
	@Invoker
	default void callAcceptParsedFile(Identifier id, JsonObject json) {
		throw new UnsupportedOperationException();
	}
}
