package io.github.vampirestudios.obsidian.mixins;

import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.network.ServerLoginNetworkHandler;

@Mixin(ServerLoginNetworkHandler.class)
public interface GameProfileAccessor {
	@Accessor
	GameProfile getProfile();
}