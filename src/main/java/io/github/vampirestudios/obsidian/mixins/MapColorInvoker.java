package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MapColor.class)
public interface MapColorInvoker {
    @Invoker("<init>")
    public static MapColor callMapColor(int id, int color) {
        throw new AssertionError();
    }
}