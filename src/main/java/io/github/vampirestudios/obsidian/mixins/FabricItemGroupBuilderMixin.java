package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.addon_modules.ExpandedItemGroups;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(value = FabricItemGroupBuilder.class)
public class FabricItemGroupBuilderMixin {

    @Shadow
    private Identifier identifier;

    @Shadow(remap = false)
    private Supplier<ItemStack> stackSupplier;

    @Inject(method = "build()Lnet/minecraft/item/ItemGroup;", at = @At("RETURN"), cancellable = true)
    private void afterConstructor(CallbackInfoReturnable<ItemGroup> cir) {
        final var createdGroup = ExpandedItemGroups.ExpandedTabs.onGroupCreated(String.format("%s.%s", identifier.getNamespace(), identifier.getPath()), ItemGroup.GROUPS.length - 1, stackSupplier);
        if (createdGroup != null) cir.setReturnValue(createdGroup);
    }

}