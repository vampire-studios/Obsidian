package io.github.vampirestudios.obsidian.breakout;

import com.raphydaphy.breakoutapi.BreakoutAPI;
import com.raphydaphy.breakoutapi.breakout.GUIBreakout;
import com.raphydaphy.breakoutapi.breakout.window.BreakoutWindow;
import net.minecraft.util.Identifier;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.color.ColorUtil;
import org.liquidengine.legui.style.font.FontRegistry;
import org.liquidengine.legui.theme.Themes;
import org.liquidengine.legui.theme.colored.FlatColoredTheme;

public class BlockBreakout extends GUIBreakout {
	private DemoPanel gui;

	public BlockBreakout(Identifier identifier) {
		super(identifier, new BreakoutWindow("Block Generator", 480, 720));
		this.window.setIcon(new Identifier(BreakoutAPI.MODID, "textures/icons/window_icon_16x16.png"), new Identifier(BreakoutAPI.MODID, "textures/icons/window_icon_32x32.png"));
		this.window.setRelativePos(0, 0);
		Themes.setDefaultTheme(
				new FlatColoredTheme(
						ColorUtil.fromInt(49, 49, 49, 1.0F),
						ColorUtil.fromInt(97, 97, 97, 1.0F),
						ColorUtil.fromInt(97, 97, 97, 1.0F),
						ColorUtil.fromInt(2, 119, 189, 1.0F),
						ColorUtil.fromInt(27, 94, 32, 1.0F),
						ColorUtil.fromInt(183, 28, 28, 1.0F),
						ColorUtil.fromInt(250, 250, 250, 0.5F),
						ColorConstants.white(),
						FontRegistry.getDefaultFont(),
						16.0F
				)
		);
	}

	@Override
	protected void createGuiElements(int width, int height) {
		this.gui = new DemoPanel(width, height);
		this.gui.setFocusable(false);
        /*this.gui.getListenerMap().addListener(WindowSizeEvent.class, (event) -> {
            this.gui.setSize((float)event.getWidth(), (float)event.getHeight());
        });*/
		this.frame.getContainer().add(this.gui);
	}

}
