package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {
	@Inject(method = "validateWrite()V", at = @At("HEAD"), cancellable = true)
	private void disableThatThing(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "validateWrite(Lnet/minecraft/resources/ResourceKey;)V", at = @At("HEAD"), cancellable = true)
	private void disableThatThing1(CallbackInfo ci) {
		ci.cancel();
	}


	@Shadow
	private boolean frozen;

	/** Make freeze() a no-op and keep registry writable. */
	@Inject(method = "freeze", at = @At("HEAD"), cancellable = true)
	private void yourmod$skipFreeze(CallbackInfoReturnable<Registry<T>> cir) {
		this.frozen = false;                 // ensure guards read "not frozen"
		cir.setReturnValue((Registry<T>) this);
	}
}
