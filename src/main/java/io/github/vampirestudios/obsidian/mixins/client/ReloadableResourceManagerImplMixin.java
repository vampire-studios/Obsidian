package io.github.vampirestudios.obsidian.mixins.client;

import com.mojang.datafixers.util.Unit;
import io.github.vampirestudios.obsidian.client.resource.BedrockAddonResourcePack;
import io.github.vampirestudios.obsidian.client.resource.ObsidianAddonResourcePack;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {

    @Inject(method = "reload", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
    private void registerAdditionalPacks(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir) {
        System.out.println("Count before: " + packs.size());
        this.addPack(new ObsidianAddonResourcePack());
        this.addPack(new BedrockAddonResourcePack());
        System.out.println("Count after: " + packs.size());
    }

    @Shadow
    public abstract void addPack(ResourcePack resourcePack);

}
