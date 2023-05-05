package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PackType.class)
public interface PackTypeAccessor {
    @Invoker("<init>")
    static PackType createPackType(String directory) {
        throw new UnsupportedOperationException();
    }
}
