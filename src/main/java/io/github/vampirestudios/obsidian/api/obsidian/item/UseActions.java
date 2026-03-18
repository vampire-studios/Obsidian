package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemUseAnimation;

import java.util.Map;

public class UseActions {

	public String use_animation;
	public Integer use_duration;

	public String right_click_actions;
	public int gui_size;
	public GuiType guiType;
	public NameInformation gui_title;
	public String url;
	public String command;

	private static final Map<String, ItemUseAnimation> USE_ANIMATION_MAP = Map.of(
			"none", ItemUseAnimation.NONE,
			"eat", ItemUseAnimation.EAT,
			"drink", ItemUseAnimation.DRINK,
			"block", ItemUseAnimation.BLOCK,
			"bow", ItemUseAnimation.BOW,
			"spear", ItemUseAnimation.SPEAR,
			"crossbow", ItemUseAnimation.CROSSBOW,
			"spyglass", ItemUseAnimation.SPYGLASS,
			"toot_horn", ItemUseAnimation.TOOT_HORN,
			"brush", ItemUseAnimation.BRUSH
	);

	public ItemUseAnimation getUseAnimation() {
		ItemUseAnimation useAnim = USE_ANIMATION_MAP.get(use_animation);
		if (useAnim == null) {
			throw new IllegalStateException("Unexpected value: " + use_animation);
		}
		return useAnim;
	}

	private static final Map<GuiType, TriFunction<Integer, Integer, Inventory, AbstractContainerMenu, ContainerLevelAccess>> GUI_TYPE_TO_MENU_MAP = Map.ofEntries(
			Map.entry(GuiType.CHEST, (syncId, gui_size, inventory, containerLevelAccess) -> switch (gui_size) {
				case 1 -> MenuType.GENERIC_9x1.create(syncId, inventory);
				case 2 -> MenuType.GENERIC_9x2.create(syncId, inventory);
				case 3 -> MenuType.GENERIC_9x3.create(syncId, inventory);
				case 4 -> MenuType.GENERIC_9x4.create(syncId, inventory);
				case 5 -> MenuType.GENERIC_9x5.create(syncId, inventory);
				case 6 -> MenuType.GENERIC_9x6.create(syncId, inventory);
				default -> throw new IllegalStateException("Unexpected value: " + gui_size);
			}),
			Map.entry(GuiType.DISPENSER, (syncId, gui_size, inventory, containerLevelAccess) -> MenuType.GENERIC_3x3.create(syncId, inventory)),
			Map.entry(GuiType.ANVIL, (syncId, gui_size, inventory, containerLevelAccess) -> new AnvilMenu(syncId, inventory, containerLevelAccess)),
			Map.entry(GuiType.CRAFTING, (syncId, gui_size, inventory, containerLevelAccess) -> new CraftingMenu(syncId, inventory, containerLevelAccess)),
			Map.entry(GuiType.SMITHING, (syncId, gui_size, inventory, containerLevelAccess) -> new SmithingMenu(syncId, inventory, containerLevelAccess)),
			Map.entry(GuiType.CARTOGRAPHY, (syncId, gui_size, inventory, containerLevelAccess) -> new CartographyTableMenu(syncId, inventory, containerLevelAccess)),
			Map.entry(GuiType.STONECUTTER, (syncId, gui_size, inventory, containerLevelAccess) -> new StonecutterMenu(syncId, inventory, containerLevelAccess)),
			Map.entry(GuiType.SMOKER, (syncId, gui_size, inventory, containerLevelAccess) -> MenuType.SMOKER.create(syncId, inventory)),
			Map.entry(GuiType.BLAST_FURNACE, (syncId, gui_size, inventory, containerLevelAccess) -> MenuType.BLAST_FURNACE.create(syncId, inventory)),
			Map.entry(GuiType.FURNACE, (syncId, gui_size, inventory, containerLevelAccess) -> MenuType.FURNACE.create(syncId, inventory))
	);

	public SimpleMenuProvider openGui(ContainerLevelAccess containerLevelAccess) throws IllegalStateException {
		TriFunction<Integer, Integer, Inventory, AbstractContainerMenu, ContainerLevelAccess> menuCreator = GUI_TYPE_TO_MENU_MAP.get(guiType);
		if (menuCreator == null) {
			throw new IllegalStateException(STR."Unexpected value: \{guiType}");
		}
		return new SimpleMenuProvider((syncId, _, playerx) -> menuCreator.apply(syncId, gui_size, playerx.getInventory(), containerLevelAccess), gui_title.getName("gui", null));
	}

	public enum GuiType {
		CHEST,
		DISPENSER,
		ANVIL,
		CRAFTING,
		SMITHING,
		CARTOGRAPHY,
		STONECUTTER,
		MERCHANT,
		SMOKER,
		BLAST_FURNACE,
		FURNACE
	}


	@FunctionalInterface
	public interface TriFunction<T, U, V, R, S> {
		R apply(T t, U u, V v, S s);
	}
}
