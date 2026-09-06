package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemUseAnimation;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
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
		if (use_animation == null) return ItemUseAnimation.NONE;
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
			Map.entry(GuiType.FURNACE, (syncId, gui_size, inventory, containerLevelAccess) -> MenuType.FURNACE.create(syncId, inventory)),
			Map.entry(GuiType.MERCHANT, (syncId, gui_size, inventory, containerLevelAccess) -> MenuType.MERCHANT.create(syncId, inventory)),
			Map.entry(GuiType.BREWING_STAND, (syncId, gui_size, inventory, containerLevelAccess) -> MenuType.BREWING_STAND.create(syncId, inventory)),
			Map.entry(GuiType.HOPPER, (syncId, gui_size, inventory, containerLevelAccess) -> MenuType.HOPPER.create(syncId, inventory)),
			Map.entry(GuiType.SHULKER_BOX, (syncId, gui_size, inventory, containerLevelAccess) -> MenuType.SHULKER_BOX.create(syncId, inventory)),
			Map.entry(GuiType.CRAFTER, (syncId, gui_size, inventory, containerLevelAccess) -> MenuType.CRAFTER_3x3.create(syncId, inventory)),
			Map.entry(GuiType.BEACON, (syncId, gui_size, inventory, containerLevelAccess) -> new BeaconMenu(syncId, inventory)),
			Map.entry(GuiType.ENCHANTMENT, (syncId, gui_size, inventory, containerLevelAccess) -> new EnchantmentMenu(syncId, inventory, containerLevelAccess)),
			Map.entry(GuiType.GRINDSTONE, (syncId, gui_size, inventory, containerLevelAccess) -> new GrindstoneMenu(syncId, inventory, containerLevelAccess)),
			Map.entry(GuiType.LOOM, (syncId, gui_size, inventory, containerLevelAccess) -> new LoomMenu(syncId, inventory, containerLevelAccess))
	);

	/** Builds a menu provider for one of the vanilla {@link GuiType}s, for the {@code open_gui} action. */
	public static SimpleMenuProvider createMenuProvider(GuiType guiType, int guiSize, Component title, ContainerLevelAccess containerLevelAccess) throws IllegalStateException {
		TriFunction<Integer, Integer, Inventory, AbstractContainerMenu, ContainerLevelAccess> menuCreator = GUI_TYPE_TO_MENU_MAP.get(guiType);
		if (menuCreator == null) {
			throw new IllegalStateException("Unexpected value: " + guiType);
		}
		return new SimpleMenuProvider((syncId, _, playerx) -> menuCreator.apply(syncId, guiSize, playerx.getInventory(), containerLevelAccess), title);
	}

	/** Whether {@code right_click_actions} asks for anything at all. */
	public boolean hasRightClickAction() {
		return right_click_actions != null && !right_click_actions.isEmpty();
	}

	public boolean opensUrl() {
		return "open_url".equals(right_click_actions);
	}

	public boolean opensGui() {
		return "open_gui".equals(right_click_actions);
	}

	/**
	 * The {@code right_click_actions} shorthand expressed as an ordinary event action, so the shorthand and
	 * the {@code events} list run the same code rather than two switches that can drift apart.
	 *
	 * <p>Null when there is nothing to run server-side: no shorthand at all, an unrecognised one, or
	 * {@code open_url}, which has to happen on the client and so stays with the item.
	 */
	public @Nullable Map<String, Object> toActionConfig() {
		if (!hasRightClickAction()) return null;

		switch (right_click_actions) {
			case "open_gui" -> {
				Map<String, Object> config = new HashMap<>();
				config.put("action", "open_gui");
				config.put("gui_type", guiType != null ? guiType.name().toLowerCase(Locale.ROOT) : "chest");
				// The shorthand's default is 0, which is not a chest size; the action's own default is 3.
				config.put("gui_size", gui_size > 0 ? gui_size : 3);
				if (gui_title != null) config.put("title", gui_title.getName("gui", null).getString());
				return config;
			}
			case "run_command" -> {
				Map<String, Object> config = new HashMap<>();
				config.put("action", "execute_command");
				config.put("command", command);
				return config;
			}
			default -> {
				return null;
			}
		}
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
		FURNACE,
		BREWING_STAND,
		HOPPER,
		SHULKER_BOX,
		CRAFTER,
		BEACON,
		ENCHANTMENT,
		GRINDSTONE,
		LOOM
	}


	@FunctionalInterface
	public interface TriFunction<T, U, V, R, S> {
		R apply(T t, U u, V v, S s);
	}
}
