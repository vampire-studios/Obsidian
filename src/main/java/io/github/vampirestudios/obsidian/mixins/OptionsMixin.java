package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Options.class)
public class OptionsMixin {

    @Shadow public List<String> resourcePacks;

    @Inject(method = "load", at = @At("RETURN"))
    private void obsidian_onLoad(CallbackInfo ci) {
        ObsidianAddonLoader.OBSIDIAN_ADDONS.forEach(iAddonPack -> resourcePacks.add(iAddonPack.getVirtualResourcePack().packId()));
    }
}
