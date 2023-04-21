package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.ExtendedRegistryEntryReference;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Holder.Reference.class)
public class ReferenceRegistryEntryMixin implements ExtendedRegistryEntryReference {
    private boolean obsidian$poisoned = false;

    @Override
    public void obsidian$poison() {
        obsidian$poisoned = true;
    }

    // Methods will be transformed via ASM.
    private void obsidian$checkPoisoned() {
        if (obsidian$poisoned)
            throw new IllegalStateException("Tried to use poisoned registry entry!");
    }
}