package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.TabbedGroup;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import io.wispforest.owo.itemgroup.json.WrapperGroup;
import io.wispforest.owo.moddata.ModDataConsumer;
import io.wispforest.owo.moddata.ModDataLoader;
import io.wispforest.owo.util.pond.OwoItemExtensions;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class ExpandedItemGroups implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        TabbedGroup itemGroup = Obsidian.GSON.fromJson(new FileReader(file), TabbedGroup.class);
        JsonObject jsonObject = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);

        try {
            if (itemGroup == null) return;

            ExpandedTabs groupTabLoader = new ExpandedTabs(itemGroup);
            groupTabLoader.acceptParsedFile(null, jsonObject);
            ModDataLoader.load(groupTabLoader);
            register(io.github.vampirestudios.obsidian.registry.Registries.EXPANDED_ITEM_GROUPS, "tabbed_group", new ResourceLocation(id.modId(), "tabbed_" + itemGroup.targetGroup), itemGroup);
        } catch (Exception e) {
            failedRegistering("tabbed_group", "tabbed_" + itemGroup.targetGroup, e);
        }
    }

    @Override
    public String getType() {
        return "item_groups/expanded";
    }

    public static class ExpandedTabs implements ModDataConsumer {
        private static TabbedGroup tabbedGroup;

        public static final ExpandedTabs INSTANCE = new ExpandedTabs(null);
        private static final Map<ResourceLocation, JsonObject> BUFFERED_GROUPS = new HashMap<>();

        public ExpandedTabs(TabbedGroup tabbedGroupIn) {
            tabbedGroup = tabbedGroupIn;
        }

        public static void onGroupCreated(CreativeModeTab group) {
            var groupId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group);

            if (!BUFFERED_GROUPS.containsKey(groupId)) return;
            INSTANCE.acceptParsedFile(groupId, BUFFERED_GROUPS.remove(groupId));
        }

        @Override
        public String getDataSubdirectory() {
            return "item_group_tabs";
        }

        @Override
        public void acceptParsedFile(ResourceLocation id, JsonObject json) {
            var tabs = new ArrayList<ItemGroupTab>();
            var buttons = new ArrayList<ItemGroupButton>();

            var targetGroupId = ResourceLocation.tryParse(tabbedGroup.targetGroup);

            CreativeModeTab searchGroup = null;
            for (CreativeModeTab group : CreativeModeTabs.allTabs()) {
                if (Objects.equals(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group), targetGroupId)) {
                    searchGroup = group;
                    break;
                }
            }

            if (searchGroup == null) {
                BUFFERED_GROUPS.put(targetGroupId, json);
                return;
            }

            final var targetGroup = searchGroup;

            for (TabbedGroup.Tab tab : tabbedGroup.tabs) {
                tabs.add(new ItemGroupTab(
                        tab.icon.getIcon(),
                        OwoItemGroup.ButtonDefinition.tooltipFor(targetGroup, "tab", tab.name),
                        (context, entries) -> BuiltInRegistries.ITEM.stream().filter(item -> item.builtInRegistryHolder().is(tab.contentTag)).forEach(entries::accept),
                        tab.texture,
                        false
                ));
            }
            if (tabbedGroup.buttons != null) {
                for (TabbedGroup.Button button : tabbedGroup.buttons) {
                    buttons.add(ItemGroupButton.link(targetGroup, button.icon.getIcon(), button.name, button.link));
                }
            }

            for (CreativeModeTab group : CreativeModeTabs.allTabs()) {
                if (!BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group).toString().equals(tabbedGroup.targetGroup)) continue;
                final var wrapperGroup = new WrapperGroup(group, ResourceLocation.tryParse(tabbedGroup.targetGroup), tabs, buttons);
                wrapperGroup.initialize();

                BuiltInRegistries.ITEM.stream()
                        .filter(item -> ((OwoItemExtensions) item).owo$group() == targetGroup)
                        .forEach(item -> ((OwoItemExtensions) item).owo$setGroup(wrapperGroup));
                return;
            }

            if (targetGroup instanceof WrapperGroup wrapper) {
                wrapper.addTabs(tabs);
                wrapper.addButtons(buttons);

//                if (GsonHelper.getAsBoolean(json, "extend", false)) wrapper.markExtension();
            } else {
                var wrapper = new WrapperGroup(targetGroup, targetGroupId, tabs, buttons);
                wrapper.initialize();
//                if (GsonHelper.getAsBoolean(json, "extend", false)) wrapper.markExtension();

                BuiltInRegistries.ITEM.stream()
                        .filter(item -> ((OwoItemExtensions) item).owo$group() == targetGroup)
                        .forEach(item -> ((OwoItemExtensions) item).owo$setGroup(wrapper));
            }
        }

        static {
            RegistryEntryAddedCallback.event(BuiltInRegistries.CREATIVE_MODE_TAB).register((rawId, id, group) -> ExpandedTabs.onGroupCreated(group));
        }
    }

}
