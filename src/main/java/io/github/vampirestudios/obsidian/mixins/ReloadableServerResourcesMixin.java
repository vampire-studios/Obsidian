package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
	@Inject(at = @At("HEAD"), method = "loadResources")
	private static void filament$loadResources(ResourceManager resourceManager, LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, List<Registry.PendingTags<?>> list, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, PermissionSet permissionSet, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir) {
//        ((RegistryUnfreezer)BuiltInRegistries.BLOCK).obsidian$unfreeze();
//        ((RegistryUnfreezer)BuiltInRegistries.ITEM).obsidian$unfreeze();
//        ((RegistryUnfreezer)BuiltInRegistries.BLOCK_ENTITY_TYPE).obsidian$unfreeze();
//        ((RegistryUnfreezer)BuiltInRegistries.CREATIVE_MODE_TAB).obsidian$unfreeze();
	}
}