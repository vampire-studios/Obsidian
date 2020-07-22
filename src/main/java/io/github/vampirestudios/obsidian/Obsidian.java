package io.github.vampirestudios.obsidian;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.vampirestudios.obsidian.api.RenderTooltipCallback;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Obsidian implements ModInitializer {

    public static String MOD_ID = "obsidian";
    public static String NAME = "Obsidian";
    public static final Logger LOGGER = LogManager.getLogger("[" + NAME + "]");
    public static String VERSION = "0.2.0";

//    public static final DataPackModdingManager MODDING_MANAGER = new DataPackModdingManager();

    @Override
    public void onInitialize() {
        LOGGER.info(String.format("You're now running %s v%s for %s", NAME, VERSION, "20w28a"));

//        MODDING_MANAGER.registerReloadListener();
        ConfigHelper.loadDefault();
        CompletableFuture.runAsync(ConfigHelper::loadConfig, ConfigHelper.EXECUTOR_SERVICE);

        Identifier RES_MAP_BACKGROUND = new Identifier("textures/map/map_background.png");

        RenderTooltipCallback.PostText.EVENT.register((postText) -> {
            if(!postText.getStack().isEmpty() && postText.getStack().getItem() instanceof FilledMapItem) {
                MinecraftClient mc = MinecraftClient.getInstance();

                MapState mapdata = FilledMapItem.getMapState(postText.getStack(), Objects.requireNonNull(mc.world));
                if(mapdata == null)
                    return;

                postText.getMatrixStack().push();
                RenderSystem.color3f(1F, 1F, 1F);
                DiffuseLighting.disable();
                mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();

                int pad = 7;
                float size = 135;
                float scale = 0.5F;

                RenderSystem.translatef(postText.getX(), postText.getY() - size * scale - 5, 500);
                RenderSystem.scalef(scale, scale, 1F);
                RenderSystem.enableBlend();

                buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
                buffer.vertex(-pad, size, 0.0D).texture(0.0F, 1.0f).next();
                buffer.vertex(size, size, 0.0D).texture(1.0F, 1.0f).next();
                buffer.vertex(size, -pad, 0.0D).texture(1.0F, 0.0F).next();
                buffer.vertex(-pad, -pad, 0.0D).texture(0.0F, 0.0F).next();
                tessellator.draw();

                VertexConsumerProvider.Immediate immediateBuffer = VertexConsumerProvider.immediate(buffer);
                mc.gameRenderer.getMapRenderer().draw(new MatrixStack(), immediateBuffer, mapdata, true, 15728880);
                immediateBuffer.draw();

                RenderSystem.disableBlend();
                RenderSystem.enableLighting();
                postText.getMatrixStack().pop();
            }
        });
    }

}