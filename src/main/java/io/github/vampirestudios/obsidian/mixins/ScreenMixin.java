/*
package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.GuiUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow public abstract void renderTooltip(MatrixStack matrices, List<? extends StringRenderable> lines, int x, int y);

    @Shadow public int width;
    @Shadow public int height;
    @Shadow protected TextRenderer textRenderer;

    @Shadow public abstract List<Text> getTooltipFromItem(ItemStack stack);

    */
/**
     * @author Olivia
     *//*

    @Overwrite
    public void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        GuiUtils.preItemToolTip(stack);
        this.renderTooltip(matrices, this.getTooltipFromItem(stack), x, y);
        GuiUtils.postItemToolTip();
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V", at=@At("HEAD"))
    public void onTooltipRender(MatrixStack matrices, List<? extends StringRenderable> lines, int x, int y, CallbackInfo info) {
        GuiUtils.drawHoveringText(matrices, lines, x, y, width, height, -1, textRenderer);
    }

}
*/
