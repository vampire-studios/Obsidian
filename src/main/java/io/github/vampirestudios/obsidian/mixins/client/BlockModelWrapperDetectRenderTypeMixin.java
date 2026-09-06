/*
package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.MultiAtlasState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(BlockModelWrapper.class)
public abstract class BlockModelWrapperDetectRenderTypeMixin {

    @Shadow
    @Final
    private static Function<ItemStack, RenderType> ITEM_RENDER_TYPE_GETTER;
    @Shadow
    @Final
    private static Function<ItemStack, RenderType> BLOCK_RENDER_TYPE_GETTER;
    @Unique private static final Identifier ITEMS_ATLAS = Identifier.withDefaultNamespace("textures/atlas/items.png");
    @Unique private static final Identifier BLOCKS_ATLAS = Identifier.withDefaultNamespace("textures/atlas/blocks.png");

    */
/**
 * Bypasses the IllegalStateException and sets MultiAtlasState.MIXED when both atlases are detected.
 *
 * NOTE: We deliberately return a "reasonable default" RenderType for item models.
 * The *correct* atlas binding per-quad is handled by the ItemRenderer mixin (section 4).
 *//*

    @Inject(
        method = "detectRenderType",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void obsidian$detectRenderTypeNoThrow(List<BakedQuad> quads, CallbackInfoReturnable<Function<ItemStack, RenderType>> cir) {
        Identifier first = null;
        boolean hasItems = false;
        boolean hasBlocks = false;

        for (BakedQuad q : quads) {
            TextureAtlasSprite s = q.sprite();
            // In mojmap, this is commonly atlasLocation(); if your IDE says no, see note below.
            Identifier atlas = s.atlasLocation();

            if (atlas.equals(ITEMS_ATLAS)) hasItems = true;
            if (atlas.equals(BLOCKS_ATLAS)) hasBlocks = true;

            if (first == null) first = atlas;
            else if (!first.equals(atlas)) {
                MultiAtlasState.markMixed();
            }
        }

        // Default: prefer items sheet if present, else fall back to blocks
        if (hasItems) {
            // items atlas render type
            cir.setReturnValue(ITEM_RENDER_TYPE_GETTER);
        } else if (hasBlocks) {
            // blocks atlas render type (fallback)
            cir.setReturnValue(BLOCK_RENDER_TYPE_GETTER);
        } else {
            // no quads -> pick item
            cir.setReturnValue(ITEM_RENDER_TYPE_GETTER);
        }
    }
}
*/
