package io.github.vampirestudios.obsidian.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMultiAtlasMixin {
    private static final Identifier ITEMS_ATLAS = Identifier.withDefaultNamespace("textures/atlas/items.png");
    private static final Identifier BLOCKS_ATLAS = Identifier.withDefaultNamespace("textures/atlas/blocks.png");

	@Shadow
	public static VertexConsumer getFoilBuffer(MultiBufferSource multiBufferSource, RenderType renderType, boolean bl, boolean bl2) {
		return null;
	}

	@Shadow
	protected static boolean useTransparentGlint(RenderType renderType) {
		return false;
	}

	@Shadow
	protected static void renderQuadList(PoseStack poseStack, VertexConsumer vertexConsumer, List<BakedQuad> list, int[] is, int i, int j) {
	}

	@Inject(
            method = "renderItem(Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II[ILjava/util/List;Lnet/minecraft/client/renderer/rendertype/RenderType;Lnet/minecraft/client/renderer/item/ItemStackRenderState$FoilType;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void obsidian$renderMixedAtlases(
            ItemDisplayContext ctx,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int light,
            int overlay,
            int[] tints,
            List<BakedQuad> quads,
            RenderType incomingRenderType,
            ItemStackRenderState.FoilType foilType,
            CallbackInfo ci
    ) {
        boolean hasItems = false;
        boolean hasBlocks = false;

        for (BakedQuad q : quads) {
            Identifier atlas = q.sprite().atlasLocation();
            if (atlas.equals(ITEMS_ATLAS)) hasItems = true;
            else if (atlas.equals(BLOCKS_ATLAS)) hasBlocks = true;
            if (hasItems && hasBlocks) break;
        }

        if (!(hasItems && hasBlocks)) return; // not mixed => vanilla path is fine

        // Split quads
        List<BakedQuad> items = new ArrayList<>();
        List<BakedQuad> blocks = new ArrayList<>();
        for (BakedQuad q : quads) {
            Identifier atlas = q.sprite().atlasLocation();
            if (atlas.equals(BLOCKS_ATLAS)) blocks.add(q);
            else items.add(q); // default to items
        }

        // Decide per-atlas render type.
        // Keep translucency choice consistent with what vanilla picked, but swap sheet.
        // If you need cutout vs translucent correctness, we can expand this.
        RenderType itemsType = Sheets.translucentItemSheet();
        RenderType blocksType = Sheets.translucentBlockItemSheet();

        // Render pass function (copied from vanilla flow, but for provided list + rendertype)
        renderPass(ctx, poseStack, buffers, light, overlay, tints, items, itemsType, foilType);
        renderPass(ctx, poseStack, buffers, light, overlay, tints, blocks, blocksType, foilType);

        ci.cancel();
    }

    private static void renderPass(
            ItemDisplayContext ctx,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int light,
            int overlay,
            int[] tints,
            List<BakedQuad> quads,
            RenderType renderType,
            ItemStackRenderState.FoilType foilType
    ) {
        if (quads.isEmpty()) return;

        VertexConsumer vc;
        if (foilType == ItemStackRenderState.FoilType.SPECIAL) {
            PoseStack.Pose pose = poseStack.last().copy();
            if (ctx == ItemDisplayContext.GUI) {
                MatrixUtil.mulComponentWise(pose.pose(), 0.5F);
            } else if (ctx.firstPerson()) {
                MatrixUtil.mulComponentWise(pose.pose(), 0.75F);
            }

            vc = VertexMultiConsumer.create(
                    new SheetedDecalTextureGenerator(
                            buffers.getBuffer(useTransparentGlint(renderType) ? RenderTypes.glintTranslucent() : RenderTypes.glint()),
                            pose,
                            0.0078125F
                    ),
                    buffers.getBuffer(renderType)
            );
        } else {
            vc = getFoilBuffer(buffers, renderType, true, foilType != ItemStackRenderState.FoilType.NONE);
        }

        renderQuadList(poseStack, vc, quads, tints, light, overlay);
    }
}
