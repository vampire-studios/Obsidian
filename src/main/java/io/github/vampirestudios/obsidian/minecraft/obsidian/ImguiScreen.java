package io.github.vampirestudios.obsidian.minecraft.obsidian;

import imgui.type.ImBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ImguiScreen extends Screen {
    public ImguiScreen() {
        super(Text.literal("ImguiScreen"));
    }

    @Override
    public void render(MatrixStack matrixStack, int i, int j, float f) {
        ExampleCanvasEditor.show(new ImBoolean(true));
    }
}