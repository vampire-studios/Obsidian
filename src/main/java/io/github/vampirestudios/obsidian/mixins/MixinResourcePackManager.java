/*
package io.github.vampirestudios.obsidian.mixins;

import com.google.common.collect.ImmutableSet;
import io.github.vampirestudios.obsidian.client.resource.AddonResourcePackCreator;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;

@Mixin(ResourcePackManager.class)
public class MixinResourcePackManager {
    private static final AddonResourcePackCreator addonClientProvider = new AddonResourcePackCreator();

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet;copyOf([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;", opcode = 1))
    public <E> ImmutableSet<Object> appendArtificePacks(E[] elements) {
        boolean isForClient2 = Arrays.stream(elements).anyMatch(element -> element instanceof ClientBuiltinResourcePackProvider);
        if(isForClient2) {
            return ImmutableSet.copyOf(ArrayUtils.add(elements, addonClientProvider));
        } else {
            return ImmutableSet.of(elements);
        }
    }
}*/
