/*
package io.github.vampirestudios.obsidian.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringRenderable;

import java.util.List;

public interface RenderTooltipCallback {

    interface Pre {
        Event<RenderTooltipCallback.Pre> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.Pre.class, (callbacks) -> {
            return (stack, lines, matrixStack, x, y, screenWidth, screenHeight, maxWidth, textRenderer) -> {
                for(RenderTooltipCallback.Pre callback : callbacks) {
                    return callback.preRender(stack, lines, matrixStack, x, y, screenWidth, screenHeight, maxWidth, textRenderer);
                }
                return new TooltipRenderer.Pre(stack, lines, matrixStack, x, y, screenWidth, screenHeight, maxWidth, textRenderer);
            };
        });

        TooltipRenderer.Pre preRender(ItemStack stack, List<? extends StringRenderable> lines, MatrixStack matrixStack, int x, int y, int screenWidth, int screenHeight, int maxWidth, TextRenderer fr);
    }

    interface PreNew {
        Event<RenderTooltipCallback.PreNew> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.PreNew.class, (callbacks) -> {
            return (stack, lines, matrixStack, x, y, screenWidth, screenHeight, maxWidth, textRenderer) -> {
                for(RenderTooltipCallback.PreNew callback : callbacks) {
                    callback.preRender(stack, lines, matrixStack, x, y, screenWidth, screenHeight, maxWidth, textRenderer);
                }
            };
        });

        void preRender(ItemStack stack, List<? extends StringRenderable> lines, MatrixStack matrixStack, int x, int y, int screenWidth, int screenHeight, int maxWidth, TextRenderer fr);
    }

    interface PreNewNew {
        Event<RenderTooltipCallback.PreNewNew> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.PreNewNew.class, (callbacks) -> {
            return (tooltipRenderer) -> {
                for(RenderTooltipCallback.PreNewNew callback : callbacks) {
                    callback.preRender(tooltipRenderer);
                }
            };
        });

        void preRender(TooltipRenderer.Pre tooltipRenderer);
    }

    interface Post {
        interface PostBackground {
            Event<RenderTooltipCallback.Post.PostBackground> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.Post.PostBackground.class, (callbacks) -> {
                return (stack, lines, matrixStack, x, y, textRenderer, width, height) -> {
                    for(RenderTooltipCallback.Post.PostBackground callback : callbacks) {
                        return callback.postBackground(stack, lines, matrixStack, x, y, textRenderer, width, height);
                    }
                    return new TooltipRenderer.PostBackground(stack, lines, matrixStack, x, y, textRenderer, width, height);
                };
            });

            TooltipRenderer.Post.PostBackground postBackground(ItemStack stack, List<? extends StringRenderable> textLines, MatrixStack matrixStack, int x, int y, TextRenderer fr, int width, int height);
        }


        */
/*interface PostText {
            Event<RenderTooltipCallback.Post.PostText> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.Post.PostText.class, (callbacks) -> {
                return (stack, lines, matrixStack, x, y, textRenderer, width, height) -> {
                    for(RenderTooltipCallback.Post.PostText callback : callbacks) {
                        return callback.postText(stack, lines, matrixStack, x, y, textRenderer, width, height);
                    }
                    return new TooltipRenderer.PostText(stack, lines, matrixStack, x, y, textRenderer, width, height);
                };
            });

            TooltipRenderer.Post.PostText postText(ItemStack stack, List<? extends StringRenderable> textLines, MatrixStack matrixStack, int x, int y, TextRenderer fr, int width, int height);
        }*//*

    }

    interface PostText {
        Event<RenderTooltipCallback.PostText> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.PostText.class, (callbacks) -> {
            return (postText) -> {
                for(RenderTooltipCallback.PostText callback : callbacks) {
                    callback.postText(postText);
                }
            };
        });

        void postText(TooltipRenderer.PostText postText);
    }

    interface PostBackground {
        Event<RenderTooltipCallback.PostBackground> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.PostBackground.class, (callbacks) -> {
            return (postBackground) -> {
                for(RenderTooltipCallback.PostBackground callback : callbacks) {
                    callback.postBackground(postBackground);
                }
            };
        });

        void postBackground(TooltipRenderer.PostBackground postBackground);
    }

    interface Color {
        Event<RenderTooltipCallback.Color> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.Color.class, (callbacks) -> {
            return (stack, lines, matrixStack, x, y, textRenderer, background, borderStart, borderEnd) -> {
                for(RenderTooltipCallback.Color callback : callbacks) {
                    return callback.color(stack, lines, matrixStack, x, y, textRenderer, background, borderStart, borderEnd);
                }
                return new TooltipRenderer.Color(stack, lines, matrixStack, x, y, textRenderer, background, borderStart, borderEnd);
            };
        });

        TooltipRenderer.Color color(ItemStack stack, List<? extends StringRenderable> textLines, MatrixStack matrixStack, int x, int y, TextRenderer fr, int background, int borderStart, int borderEnd);
    }

}
*/
