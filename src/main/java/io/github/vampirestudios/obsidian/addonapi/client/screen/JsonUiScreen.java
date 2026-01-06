package io.github.vampirestudios.obsidian.addonapi.client.screen;

import io.github.vampirestudios.obsidian.addonapi.menu.AnchorPosDef;
import io.github.vampirestudios.obsidian.addonapi.menu.ScreenMenuDefinition;
import io.github.vampirestudios.obsidian.addonapi.menu.UiElementDef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// JsonUiScreen.java
public class JsonUiScreen extends Screen {
    private final ScreenMenuDefinition definition;
    private final @Nullable Screen parent;

    private final List<UiElementDef> labelElements = new ArrayList<>();
    private final List<UiElementDef> imageElements = new ArrayList<>();

    public JsonUiScreen(ScreenMenuDefinition definition, @Nullable Screen parent) {
        super(definition.title != null ? definition.title.toMc()
                                       : Component.literal(""));
        this.definition = definition;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        labelElements.clear();
        imageElements.clear();

        int screenWidth = this.width;
        int screenHeight = this.height;

        for (UiElementDef el : definition.elements) {
            AnchorPosDef xPos = el.x != null ? el.x : defaultAnchorX();
            AnchorPosDef yPos = el.y != null ? el.y : defaultAnchorY();

            int w = el.width;
            int h = el.height;

            int x = resolveX(xPos, w, screenWidth);
            int y = resolveY(yPos, h, screenHeight);

            switch (el.type) {
                case "button" -> {
                    Component label = el.label != null ? el.label.toMc() : Component.literal(el.id);
                    Button button = Button
                            .builder(label, _ -> onElementClick(el))
							.bounds(x, y, w, h)
                            .build();
                    this.addRenderableWidget(button);
                }
                case "label" -> {
                    labelElements.add(el); // draw in render()
                }
                case "image" -> {
                    imageElements.add(el); // draw in render()
                }
                default -> {
                    // ignore unknown element types
                }
            }
        }
    }

    private AnchorPosDef defaultAnchorX() {
        AnchorPosDef p = new AnchorPosDef();
        p.anchor = "left";
        p.offset = 0;
        return p;
    }

    private AnchorPosDef defaultAnchorY() {
        AnchorPosDef p = new AnchorPosDef();
        p.anchor = "top";
        p.offset = 0;
        return p;
    }

    private int resolveX(AnchorPosDef pos, int elementWidth, int screenWidth) {
        String a = pos.anchor != null ? pos.anchor : "left";
        return switch (a) {
            case "left" -> pos.offset;
            case "center" -> (screenWidth / 2) - (elementWidth / 2) + pos.offset;
            case "right" -> screenWidth - elementWidth + pos.offset;
            default -> pos.offset;
        };
    }

    private int resolveY(AnchorPosDef pos, int elementHeight, int screenHeight) {
        String a = pos.anchor != null ? pos.anchor : "top";
        return switch (a) {
            case "top" -> pos.offset;
            case "center" -> (screenHeight / 2) - (elementHeight / 2) + pos.offset;
            case "bottom" -> screenHeight - elementHeight + pos.offset;
            default -> pos.offset;
        };
    }

    private void onElementClick(UiElementDef el) {
//		Minecraft mc = Minecraft.getInstance();
//        CommandSourceStack source = /* however you represent client-side actions, or send a packet to server */;
        // Simplest approach: send a custom packet to server with element id,
        // or directly handle client-only actions here.

        // If you already have ActionDefinition handling on server,
        // you can define a "ui_click" packet (menu id + element id) → server resolves actions.
    }

	@Override
	public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		// background
		renderBackground(ctx, mouseX, mouseY, delta);

		// labels
		for (UiElementDef el : labelElements) {
			AnchorPosDef xPos = el.x != null ? el.x : defaultAnchorX();
			AnchorPosDef yPos = el.y != null ? el.y : defaultAnchorY();

			int w = el.width;
			int h = el.height;
			int x = resolveX(xPos, w, this.width);
			int y = resolveY(yPos, h, this.height);

			Component text = el.text != null ? el.text.toMc() : Component.literal(el.id);
			ctx.drawString(this.font, text, x, y, 0xFFFFFF, false);
		}

		for (UiElementDef el : imageElements) {
			AnchorPosDef xPos = el.x != null ? el.x : defaultAnchorX();
			AnchorPosDef yPos = el.y != null ? el.y : defaultAnchorY();

			int w = el.width;
			int h = el.height;
			int x = resolveX(xPos, w, this.width);
			int y = resolveY(yPos, h, this.height);

			Identifier texId = Identifier.tryParse(el.texture);
			ctx.blit(texId, x, y, el.u, el.v, w, h, el.tex_width, el.tex_height);
		}
		super.render(ctx, mouseX, mouseY, delta);
	}

	@Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}
