package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.TabbedGroup;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class ExpandedItemGroups implements AddonModule {

    @Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        TabbedGroup itemGroup = Obsidian.GSON.fromJson(new FileReader(file), TabbedGroup.class);
        JsonObject jsonObject = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);

        try {
            if (itemGroup == null) return;

//            ExpandedTabs groupTabLoader = new ExpandedTabs(itemGroup);
//            groupTabLoader.acceptParsedFile(null, jsonObject);
//            ModDataLoader.load(groupTabLoader);
            register(ContentRegistries.EXPANDED_ITEM_GROUPS, "tabbed_group", new Identifier(id.modId(), "tabbed_" + itemGroup.targetGroup), itemGroup);
        } catch (Exception e) {
            failedRegistering("tabbed_group", "tabbed_" + itemGroup.targetGroup, e);
        }
    }

    @Override
    public String getType() {
        return "item_groups/expanded";
    }

	/*public static class ExpandedTabs implements ModDataConsumer {
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
					this.setTabStackHeight(tabbedGroup.stackHeight);
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
							TagKey.of(Registry.ITEM_KEY, tab.contentTag), tab.texture));
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
					((OwoItemExtensions) item).owo$setItemGroup(wrapperGroup);
				}

				return;
			}

			CACHED_BUTTONS.put(tabbedGroup.targetGroup, new Pair<>(createdTabs, createdButtons));
		}
	}*/

}