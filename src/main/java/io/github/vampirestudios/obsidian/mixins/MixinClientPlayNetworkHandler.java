package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.Disconnector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements Disconnector {
	@Shadow
	public abstract void onDisconnected(Text reason);

	@Shadow
	private MinecraftClient client;

	@Override
	public void config_disconnect(Text text) {
		this.client.execute(() -> this.onDisconnected(text));
	}
}