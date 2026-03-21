package io.github.vampirestudios.obsidian.addon_modules;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.Optional;

/** Shared helpers to reduce boilerplate across item-related addon modules. */
public final class ItemModuleHelper {

    private ItemModuleHelper() {}

    // -------------------------------------------------------------------------
    // Component application
    // -------------------------------------------------------------------------

    /** Applies every entry from {@code map} onto {@code props}. */
    @SuppressWarnings("unchecked")
    public static <T> void applyAllComponents(Item.Properties props, DataComponentPatch map) {
        for (var e : map.entrySet()) {
            var type = (DataComponentType<T>) e.getKey();
            var opt  = (Optional<T>) e.getValue();
            opt.ifPresent(v -> props.component(type, v));
        }
    }

    /**
     * Creates a fresh {@link Item.Properties} and applies all user-defined
     * components from {@code item.components} (if any).
     */
    public static Item.Properties baseProperties(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
        Item.Properties props = new Item.Properties();
        if (item.components != null) applyAllComponents(props, item.components);
        return props;
    }

    // -------------------------------------------------------------------------
    // Creative tab resolution
    // -------------------------------------------------------------------------

    /**
     * Resolves the creative tab for an item, checking (in order):
     * <ol>
     *   <li>The {@code OItemComponents.CREATIVE_TAB} component</li>
     *   <li>The item's own settings {@code itemGroup}</li>
     *   <li>The parent settings {@code itemGroup}</li>
     *   <li>{@code defaultTab}</li>
     * </ol>
     */
    public static ResourceKey<CreativeModeTab> getCreativeTab(
            io.github.vampirestudios.obsidian.api.obsidian.item.Item item,
            ResourceKey<CreativeModeTab> defaultTab) {
//        if (item.components != null) {
//            var opt = item.components.get(OItemComponents.CREATIVE_TAB);
//            if (opt != null && opt.isPresent()) {
//                Identifier tabId = (Identifier) Objects.requireNonNull(opt).orElseThrow();
//                return ResourceKey.create(Registries.CREATIVE_MODE_TAB, tabId);
//            }
//        }
        if (item.information.getItemSettings().getItemGroup() != null)
            return item.information.getItemSettings().getItemGroup();
        if (item.information.getItemSettings().getParentSettings().getItemGroup() != null)
            return item.information.getItemSettings().getParentSettings().getItemGroup();
        return defaultTab;
    }

    /**
     * {@link #getCreativeTab(io.github.vampirestudios.obsidian.api.obsidian.item.Item, ResourceKey)}
     * defaulting to {@link CreativeModeTabs#BUILDING_BLOCKS}.
     */
    public static ResourceKey<CreativeModeTab> getCreativeTab(
            io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
        return getCreativeTab(item, CreativeModeTabs.BUILDING_BLOCKS);
    }
}
