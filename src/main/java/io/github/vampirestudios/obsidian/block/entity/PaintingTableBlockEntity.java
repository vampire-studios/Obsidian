package io.github.vampirestudios.obsidian.block.entity;

import eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1;
import eu.pb4.placeholders.api.parsers.TagParser;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.vampirestudios.obsidian.api.obsidian.PaintingTableInformation;
import io.github.vampirestudios.obsidian.registry.OBE;
import io.github.vampirestudios.obsidian.registry.OI;
import io.github.vampirestudios.obsidian.utils.ColorUtil;
import io.github.vampirestudios.obsidian.utils.IntArray;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaintingTableBlockEntity extends BlockEntity implements Container {
	private final SimpleContainer container = new SimpleContainer(1);

	public PaintingTableBlockEntity(BlockPos pos, BlockState blockState) {
		super(OBE.PAINTING_TABLE, pos, blockState);
	}

	public InteractionResult onUse(PaintingTableInformation information, Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			new Gui(information, serverPlayer);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public int getContainerSize() {
		return container.getContainerSize();
	}

	@Override
	public boolean isEmpty() {
		return container.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return container.getItem(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return container.removeItem(slot, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return container.removeItemNoUpdate(slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		container.setItem(slot, stack);
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}

	@Override
	public void clearContent() {
		container.clearContent();
	}

	private class Gui extends SimpleGui {
		private static final String DEFAULT_TITLE = "Painting Table";
		private final PaintingTableInformation information;
		private final ServerPlayer player;

		int baseColorScrollingIndex = 0; // Reset if player was in before
		int subColorScrollingIndex = 0; // Reset if player was in before

		public Gui(PaintingTableInformation information, ServerPlayer player) {
			super(MenuType.GENERIC_9x5, player, false);
			this.information = information;
			this.player = player;

			this.setSlotRedirect(information.buttons.inputSlot, new Slot(container, 0, 0, 0));
			this.setTitle(getTitle());
			drawColorSlots();
			this.open();
		}

		public Component getTitle() {
			if (information != null && information.title != null && !information.title.trim().isEmpty()) {
				return TagParser.QUICK_TEXT_WITH_STF.parseNode(information.title).toText();
			} else {
				return Component.literal(DEFAULT_TITLE);
			}
		}

		private void drawColorSlots() {
			Map<Integer, String> colorMap = buildColorIndexMap();
			if (information.buttons.baseColorGrid.type == PaintingTableInformation.Type.NORMAL) {
				for (IntArray colorIndicesArray : information.buttons.baseColorGrid.normalGrid.rows) {
					for (int colorIndex : colorIndicesArray.getArray()) {
						PaintingTableInformation.Color baseColor = getColorCategoryForIndex(colorIndex, colorMap);
//						List<PaintingTableInformation.Color> subColors = getColorCategoryForIndex(colorIndex, colorMap);
						ItemStack colorItemStack = buildColorItemStack(baseColor);
						this.setSlot(colorIndex, GuiElementBuilder.from(colorItemStack).setCallback((index, clickType, actionType) -> {
//							drawGradientSlots(subColors, container.getItem(0), player);
						}));
					}
				}
			} else if(information.buttons.baseColorGrid.type == PaintingTableInformation.Type.SCROLLING) {
			}
		}

		private PaintingTableInformation.Color getColorCategoryForIndex(int colorIndex, Map<Integer, String> colorMap) {
			String colorKey = colorMap.get(colorIndex);
			return information.colors.baseColor.get(colorKey);
		}

		private ItemStack buildColorItemStack(PaintingTableInformation.Color color) {
			return buildColorItemStack(new ItemStack(OI.COLOR_ITEM), color, true);
		}

		private ItemStack buildColorItemStack(ItemStack itemStack, PaintingTableInformation.Color color, boolean customName) {
			itemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(ColorUtil.toIntRgb(ColorUtil.toFloatArray(color.getColor()))));
			if(customName) itemStack.set(DataComponents.ITEM_NAME, MarkdownLiteParserV1.ALL.parseNode(color.name).toText());
			return itemStack;
		}

		private Map<Integer, String> buildColorIndexMap() {
			Map<Integer, String> colorIndexMap = new HashMap<>();
			String[][] baseColorGrid2 = {
					{"12=red", "13=orange", "14=yellow"},
					{"21=pink", "22=white", "23=green"},
					{"30=purple", "31=blue", "32=light_blue"}
			};
			for (String[] row : baseColorGrid2) {
				for (String cell : row) {
					String[] parts = cell.split("=");
					if (parts.length == 2) {
						try {
							int index = Integer.parseInt(parts[0]);
							colorIndexMap.put(index, parts[1]);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return colorIndexMap;
		}

		private void drawGradientSlots(
				List<PaintingTableInformation.Color> colors,
				ItemStack hatItemStack,
				ServerPlayer player
		) {

			if (information.buttons.subColorGrid.type == PaintingTableInformation.Type.NORMAL) {
				int[] colorGradientSlots = information.buttons.subColorRow;
				for (int i = 0; i < colorGradientSlots.length; i++) {
					PaintingTableInformation.Color color = colors.get(i);
					ItemStack is4 = buildColorItemStack(color);

					this.setSlot(colorGradientSlots[i], GuiElementBuilder.from(is4).setCallback(() -> {
						ItemStack is5 = buildColorItemStack(hatItemStack.copy(), color, false);
						this.setSlot(information.buttons.outputSlot, GuiElementBuilder.from(is5).setCallback(() -> {
							player.addItem(is5);
							container.removeItem(0, 1);
							this.setSlot(information.buttons.outputSlot, ItemStack.EMPTY);
						}));
					}));
				}
			}
		}

		@Override
		public void onTick() {
			if (shouldClose()) {
				this.close();
			} else {
				super.onTick();
			}
		}

		private boolean shouldClose() {
			return isRemoved() || isPlayerTooFar(player);
		}

		private boolean isPlayerTooFar(ServerPlayer player) {
			double maxDistanceSquared = 18 * 18;
			return player.blockPosition().distToCenterSqr(Vec3.atCenterOf(PaintingTableBlockEntity.this.getBlockPos())) > maxDistanceSquared;
		}
	}
}
