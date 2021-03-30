package io.github.vampirestudios.obsidian.mixins;

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

    @Inject(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void registerAdditionalPacks(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir) {
        this.addPack(new ObsidianAddonResourcePack());
        this.addPack(new BedrockAddonResourcePack());
    }

    @Shadow
    public abstract void addPack(ResourcePack resourcePack);

}