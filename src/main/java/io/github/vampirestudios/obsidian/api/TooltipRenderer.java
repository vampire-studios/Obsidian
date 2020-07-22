package io.github.vampirestudios.obsidian.api;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringRenderable;

import java.util.Collections;
import java.util.List;

public class TooltipRenderer {

    protected final ItemStack stack;
    protected final List<? extends StringRenderable> lines;
    protected final MatrixStack matrixStack;
    protected int x;
    protected int y;
    protected TextRenderer fr;

    public TooltipRenderer(ItemStack stack, List<? extends StringRenderable> lines, MatrixStack matrixStack, int x, int y, TextRenderer fr) {
        this.stack = stack;
        this.lines = Collections.unmodifiableList(lines); // Leave editing to ItemTooltipEvent
        this.matrixStack = matrixStack;
        this.x = x;
        this.y = y;
        this.fr = fr;
    }

    /**
     * @return The stack which the tooltip is being rendered for. As tooltips can be drawn without itemstacks, this stack may be empty.
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * The lines to be drawn. May change between {@link TooltipRenderer.Pre} and {@link TooltipRenderer.Post}.
     */
    public List<? extends StringRenderable> getLines() {
        return lines;
    }

    /**
     * @return The MatrixStack of the current rendering context
     */
    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    /**
     * @return The X position of the tooltip box. By default, the mouse X position.
     */
    public int getX() {
        return x;
    }

    /**
     * @return The Y position of the tooltip box. By default, the mouse Y position.
     */
    public int getY() {
        return y;
    }

    /**
     * @return The {@link TextRenderer} instance the current render is using.
     */
    public TextRenderer getFontRenderer() {
        return fr;
    }

    /**
     * This event is fired before any tooltip calculations are done. It provides setters for all aspects of the tooltip, so the final render can be modified.
     */
    public static class Pre extends TooltipRenderer {
        private int screenWidth;
        private int screenHeight;
        private int maxWidth;

        public Pre(ItemStack stack, List<? extends StringRenderable> lines, MatrixStack matrixStack, int x, int y, int screenWidth, int screenHeight, int maxWidth, TextRenderer fr) {
            super(stack, lines, matrixStack, x, y, fr);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.maxWidth = maxWidth;
        }

        public int getScreenWidth() {
            return screenWidth;
        }

        public void setScreenWidth(int screenWidth) {
            this.screenWidth = screenWidth;
        }

        public int getScreenHeight() {
            return screenHeight;
        }

        public void setScreenHeight(int screenHeight) {
            this.screenHeight = screenHeight;
        }

        /**
         * @return The max width the tooltip can be. Defaults to -1 (unlimited).
         */
        public int getMaxWidth() {
            return maxWidth;
        }

        /**
         * Sets the max width of the tooltip. Use -1 for unlimited.
         */
        public void setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }

        /**
         * Sets the {@link TextRenderer} to be used to render text.
         */
        public void setFontRenderer(TextRenderer fr) {
            this.fr = fr;
        }

        /**
         * Set the X origin of the tooltip.
         */
        public void setX(int x) {
            this.x = x;
        }

        /**
         * Set the Y origin of the tooltip.
         */
        public void setY(int y) {
            this.y = y;
        }
    }

    /**
     * Events inheriting from this class are fired at different stages during the tooltip rendering.
     * <p>
     * Do not use this event directly, use one of its subclasses:
     * <ul>
     * <li>{@link TooltipRenderer.PostBackground}</li>
     * <li>{@link TooltipRenderer.PostText}</li>
     * </ul>
     */
    protected static abstract class Post extends TooltipRenderer {
        private final int width;
        private final int height;

        public Post(ItemStack stack, List<? extends StringRenderable> textLines, MatrixStack matrixStack, int x, int y, TextRenderer fr, int width, int height) {
            super(stack, textLines, matrixStack, x, y, fr);
            this.width = width;
            this.height = height;
        }

        /**
         * @return The width of the tooltip box. This is the width of the <i>inner</i> box, not including the border.
         */
        public int getWidth() {
            return width;
        }

        /**
         * @return The height of the tooltip box. This is the height of the <i>inner</i> box, not including the border.
         */
        public int getHeight() {
            return height;
        }
    }

    /**
     * This event is fired directly after the tooltip background is drawn, but before any text is drawn.
     */
    public static class PostBackground extends Post {
        public PostBackground(ItemStack stack, List<? extends StringRenderable> textLines, MatrixStack matrixStack, int x, int y, TextRenderer fr, int width, int height) {
            super(stack, textLines, matrixStack, x, y, fr, width, height);
        }
    }

    /**
     * This event is fired directly after the tooltip text is drawn, but before the GL state is reset.
     */
    public static class PostText extends Post {
        public PostText(ItemStack stack, List<? extends StringRenderable> textLines, MatrixStack matrixStack, int x, int y, TextRenderer fr, int width, int height) {
            super(stack, textLines, matrixStack, x, y, fr, width, height);
        }
    }

    /**
     * This event is fired when the colours for the tooltip background are determined.
     */
    public static class Color extends TooltipRenderer {
        private final int originalBackground;
        private final int originalBorderStart;
        private final int originalBorderEnd;
        private int background;
        private int borderStart;
        private int borderEnd;

        public Color(ItemStack stack, List<? extends StringRenderable> textLines, MatrixStack matrixStack, int x, int y, TextRenderer fr, int background, int borderStart, int borderEnd) {
            super(stack, textLines, matrixStack, x, y, fr);
            this.originalBackground = background;
            this.originalBorderStart = borderStart;
            this.originalBorderEnd = borderEnd;
            this.background = background;
            this.borderStart = borderStart;
            this.borderEnd = borderEnd;
        }

        public int getBackground() {
            return background;
        }

        public void setBackground(int background) {
            this.background = background;
        }

        public int getBorderStart() {
            return borderStart;
        }

        public void setBorderStart(int borderStart) {
            this.borderStart = borderStart;
        }

        public int getBorderEnd() {
            return borderEnd;
        }

        public void setBorderEnd(int borderEnd) {
            this.borderEnd = borderEnd;
        }

        public int getOriginalBackground() {
            return originalBackground;
        }

        public int getOriginalBorderStart() {
            return originalBorderStart;
        }

        public int getOriginalBorderEnd() {
            return originalBorderEnd;
        }
    }

}
