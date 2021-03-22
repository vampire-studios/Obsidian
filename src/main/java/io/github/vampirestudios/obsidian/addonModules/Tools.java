package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Tools implements AddonModule {
	@Override
	public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
		io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem tool = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem.class);
		try {
			if (tool == null) return;
			CustomToolMaterial material = new CustomToolMaterial(tool.material);
			switch (tool.toolType) {
				case "pickaxe":
					RegistryUtils.registerItem(new PickaxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
									.group(tool.information.getItemGroup()).maxCount(tool.information.max_count)),
							tool.information.name.id);
					break;
				case "shovel":
					RegistryUtils.registerItem(new ShovelItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
									.group(tool.information.getItemGroup()).maxCount(tool.information.max_count)),
							tool.information.name.id);
					break;
				case "hoe":
					RegistryUtils.registerItem(new HoeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
									.group(tool.information.getItemGroup()).maxCount(tool.information.max_count)),
							tool.information.name.id);
					break;
				case "axe":
					RegistryUtils.registerItem(new AxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
									.group(tool.information.getItemGroup()).maxCount(tool.information.max_count)),
							tool.information.name.id);
					break;
				case "bow":
					RegistryUtils.registerItem(new SimpleBowItem(new Item.Settings().group(tool.information.getItemGroup())
							.maxCount(tool.information.max_count)), tool.information.name.id);
					break;
				case "crossbow":
					RegistryUtils.registerItem(new SimpleCrossbowItem(new Item.Settings().group(tool.information.getItemGroup())
							.maxCount(tool.information.max_count)), tool.information.name.id);
					break;
				case "trident":
					RegistryUtils.registerItem(new SimpleTridentItem(new Item.Settings().group(tool.information.getItemGroup())
							.maxCount(tool.information.max_count)), tool.information.name.id);
					break;
			}
			register(TOOLS, "tool", tool.information.name.id.toString(), tool);
		} catch (Exception e) {
			failedRegistering("tool", tool.information.name.id.toString(), e);
		}
	}

	@Override
	public String getType() {
		return "items/tools";
	}
}
