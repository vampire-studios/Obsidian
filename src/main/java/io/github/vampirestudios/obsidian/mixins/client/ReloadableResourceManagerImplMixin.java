package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.client.resource.ObsidianAddonResourcePack;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(LifecycledResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static List<ResourcePack> addAdditionalPacks(List<ResourcePack> packs) {
        List<ResourcePack> packsNew = new java.util.ArrayList<>(packs);
        System.out.println("Count before: " + packsNew.size());
        ObsidianAddonLoader.OBSIDIAN_ADDONS.forEach(addonPack ->
                packsNew.add(new ObsidianAddonResourcePack(addonPack))
        );
        System.out.println("Count after: " + packsNew.size());
        return packsNew;
    }

}
