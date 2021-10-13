package io.github.vampirestudios.obsidian.addonModules;

import com.glisco.owo.itemgroup.OwoItemExtensions;
import com.glisco.owo.itemgroup.gui.ItemGroupButton;
import com.glisco.owo.itemgroup.gui.ItemGroupTab;
import com.glisco.owo.itemgroup.json.GroupTabLoader;
import com.glisco.owo.itemgroup.json.WrapperGroup;
import com.glisco.owo.moddata.ModDataConsumer;
import com.glisco.owo.moddata.ModDataLoader;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.ExpandedItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.TabbedGroup;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ExpandedItemGroups implements AddonModule {

    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        ExpandedItemGroup itemGroup = Obsidian.GSON.fromJson(new FileReader(file), ExpandedItemGroup.class);
        JsonObject jsonObject = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);

        try {
            if (itemGroup == null) return;
            GroupTabLoader groupTabLoader = new GroupTabLoader();
            groupTabLoader.acceptParsedFile(null, jsonObject);
            ModDataLoader.load(/*new ExpandedTabs(itemGroup)*/groupTabLoader);
            ObsidianAddonLoader.register(ObsidianAddonLoader.EXPANDED_ITEM_GROUPS, "expanded_item_group", new Identifier(id.modId, itemGroup.target_group.getPath() + "_expanded"), itemGroup);
        } catch (Exception e) {
            ObsidianAddonLoader.failedRegistering("expanded_item_group", itemGroup.target_group.getPath() + "_expanded", e);
        }
    }

    @Override
    public String getType() {
        return "item_groups/expanded";
    }

    public static class ExpandedTabs implements ModDataConsumer {
        private final TabbedGroup tabbedGroup;
        private static final Map<String, Pair<List<ItemGroupTab>, List<ItemGroupButton>>> CACHED_BUTTONS = new HashMap<>();

        public ExpandedTabs(TabbedGroup tabbedGroup) {
            this.tabbedGroup = tabbedGroup;
        }

        public static ItemGroup onGroupCreated(String name, int index, Supplier<ItemStack> icon) {
            if (!CACHED_BUTTONS.containsKey(name)) return null;
            final var cache = CACHED_BUTTONS.remove(name);
            final var wrapperGroup = new WrapperGroup(index, name, cache.getLeft(), cache.getRight(), icon);
            wrapperGroup.initialize();
            return wrapperGroup;
        }
        @Override
        public String getDataSubdirectory() {
            return "item_group_tabs";
        }

        @Override
        public void acceptParsedFile(Identifier id, JsonObject json) {
            List<ItemGroupTab> createdTabs = new ArrayList<>();
            List<ItemGroupButton> createdButtons = new ArrayList<>();

            for (TabbedGroup.Tab tab : tabbedGroup.tabs) {
                createdTabs.add(new ItemGroupTab(tab.icon.getIcon(), tab.name,
                        TagFactory.ITEM.create(tab.contentTag), tab.texture));
            }

            for (TabbedGroup.Button button : tabbedGroup.buttons) {
                createdButtons.add(new ItemGroupButton(button.icon.getIcon(), button.name,
                        () -> Util.getOperatingSystem().open(button.link)));
            }
            for (ItemGroup group : ItemGroup.GROUPS) {
                if (!group.getName().equals(tabbedGroup.targetGroup)) continue;
                final var wrappedGroup = new WrapperGroup(group.getIndex(), group.getName(), createdTabs, createdButtons, group::createIcon);
                wrappedGroup.initialize();

                for (Item item : Registry.ITEM) {
                    if (item.getGroup() != group) continue;
                    ((OwoItemExtensions) item).setItemGroup(wrappedGroup);
                }

                return;
            }

            CACHED_BUTTONS.put(tabbedGroup.targetGroup, new Pair<>(createdTabs, createdButtons));
        }
    }

}