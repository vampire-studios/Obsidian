package io.github.vampirestudios.obsidian.mixins;

import com.glisco.owo.itemgroup.json.GroupTabLoader;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GroupTabLoader.class)
public interface GroupTabLoaderAccessor {
	@Invoker
	static void callLoadGroups(JsonObject json) {
		throw new UnsupportedOperationException();
	}
}
