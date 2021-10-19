package io.github.vampirestudios.obsidian.addonModules;

import com.glisco.owo.itemgroup.OwoItemExtensions;
import com.glisco.owo.itemgroup.gui.ItemGroupButton;
import com.glisco.owo.itemgroup.gui.ItemGroupTab;
import com.glisco.owo.itemgroup.json.WrapperGroup;
import com.glisco.owo.moddata.ModDataConsumer;
import com.glisco.owo.moddata.ModDataLoader;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
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
        TabbedGroup itemGroup = Obsidian.GSON.fromJson(new FileReader(file), TabbedGroup.class);
        JsonObject jsonObject = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);

        try {
            if (itemGroup == null) return;
            ExpandedTabs groupTabLoader = new ExpandedTabs(itemGroup);
            groupTabLoader.acceptParsedFile(null, jsonObject);
            ModDataLoader.load(groupTabLoader);
            ObsidianAddonLoader.register(ObsidianAddonLoader.EXPANDED_ITEM_GROUPS, "tabbed_group", new Identifier(id.modId, "tabbed_" + itemGroup.targetGroup), itemGroup);
        } catch (Exception e) {
            ObsidianAddonLoader.failedRegistering("tabbed_group", "tabbed_" + itemGroup.targetGroup, e);
        }
    }

    @Override
    public String getType() {
        return "item_groups/expanded_new";
    }

	public static class ExpandedTabs implements ModDataConsumer {
    	private static TabbedGroup tabbedGroup;
		private static final Map<String, Pair<List<ItemGroupTab>, List<ItemGroupButton>>> CACHED_BUTTONS = new HashMap<>();

		public ExpandedTabs(TabbedGroup tabbedGroupIn) {
			tabbedGroup = tabbedGroupIn;
		}

		public static ItemGroup onGroupCreated(String name, int index, Supplier<ItemStack> icon) {
			if (!CACHED_BUTTONS.containsKey(name)) return null;
			final var cache = CACHED_BUTTONS.remove(name);
			final var wrapperGroup = new WrapperGroup(index, name, cache.getLeft(), cache.getRight(), icon) {
				@Override
				protected void setup() {
					super.setup();
					if (tabbedGroup.staticTitle) this.keepStaticTitle();
					this.setStackHeight(tabbedGroup.stackHeight);
					this.setCustomTexture(tabbedGroup.customTexture);
				}
			};
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

			if (tabbedGroup.tabs != null) {
				for (TabbedGroup.Tab tab : tabbedGroup.tabs) {
					createdTabs.add(new ItemGroupTab(tab.icon.getIcon(), tab.name,
							TagFactory.ITEM.create(tab.contentTag), tab.texture));
				}
			}

			if (tabbedGroup.buttons != null) {
				for (TabbedGroup.Button button : tabbedGroup.buttons) {
					createdButtons.add(new ItemGroupButton(button.icon.getIcon(), button.name,
							() -> Util.getOperatingSystem().open(button.link)));
				}
			}

			for (ItemGroup group : ItemGroup.GROUPS) {
				if (!group.getName().equals(tabbedGroup.targetGroup)) continue;
				final var wrapperGroup = new WrapperGroup(group.getIndex(), group.getName(), createdTabs, createdButtons, group::createIcon);
				wrapperGroup.initialize();

				for (Item item : Registry.ITEM) {
					if (item.getGroup() != group) continue;
					((OwoItemExtensions) item).setItemGroup(wrapperGroup);
				}

				return;
			}

			CACHED_BUTTONS.put(tabbedGroup.targetGroup, new Pair<>(createdTabs, createdButtons));
		}
	}

}