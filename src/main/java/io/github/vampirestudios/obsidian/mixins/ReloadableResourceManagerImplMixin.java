package io.github.vampirestudios.obsidian.mixins;

import com.mojang.datafixers.util.Unit;
import io.github.vampirestudios.obsidian.client.resource.AddonResourcePack;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloadMonitor;
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

    @Inject (method = "beginMonitoredReload", at = @At (value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void registerARRPs(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReloadMonitor> cir) {
        this.addPack(new AddonResourcePack());
    }

    @Shadow
    public abstract void addPack(ResourcePack resourcePack);

}