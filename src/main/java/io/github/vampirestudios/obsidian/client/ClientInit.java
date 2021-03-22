package io.github.vampirestudios.obsidian.client;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.obsidian.threadhandlers.assets.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class ClientInit implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ConfigHelper.OBSIDIAN_ADDONS.forEach(iAddonPack -> {
			String name = iAddonPack.getDisplayNameObsidian();
			Artifice.registerAssetPack(new Identifier(iAddonPack.getConfigPackInfo().namespace, iAddonPack.getConfigPackInfo().namespace), clientResourcePackBuilder -> {
				clientResourcePackBuilder.setDisplayName(name);
				for (Entity entity : ConfigHelper.ENTITIES) if (entity.information.identifier.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new EntityInitThread(clientResourcePackBuilder, entity).run();
				for (ItemGroup itemGroup : ConfigHelper.ITEM_GROUPS) if (itemGroup.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new ItemGroupInitThread(clientResourcePackBuilder, itemGroup).run();
				for (Block block : ConfigHelper.BLOCKS) if (block.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new BlockInitThread(clientResourcePackBuilder, block).run();
				for (Block block : ConfigHelper.ORES) if (block.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new BlockInitThread(clientResourcePackBuilder, block).run();
				for (Item item : ConfigHelper.ITEMS) if (item.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new ItemInitThread(clientResourcePackBuilder, item).run();
				for (ArmorItem armor : ConfigHelper.ARMORS) if (armor.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new ArmorInitThread(clientResourcePackBuilder, armor).run();
				for (WeaponItem weapon : ConfigHelper.WEAPONS) if (weapon.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new WeaponInitThread(clientResourcePackBuilder, weapon).run();
				for (ToolItem tool : ConfigHelper.TOOLS) if (tool.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new ToolInitThread(clientResourcePackBuilder, tool).run();
				for (FoodItem foodItem : ConfigHelper.FOODS) if (foodItem.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new FoodInitThread(clientResourcePackBuilder, foodItem).run();
				for (Enchantment enchantment : ConfigHelper.ENCHANTMENTS) if (enchantment.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new EnchantmentInitThread(clientResourcePackBuilder, enchantment).run();
				for (ShieldItem shield : ConfigHelper.SHIELDS) if (shield.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new ShieldInitThread(clientResourcePackBuilder, shield).run();
				for (Elytra elytra : ConfigHelper.ELYTRAS) if (elytra.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace)) new ElytraInitThread(clientResourcePackBuilder, elytra).run();
				if (FabricLoader.getInstance().isDevelopmentEnvironment()) new Thread(() -> {
					try {
						if (FabricLoader.getInstance().isDevelopmentEnvironment())
							clientResourcePackBuilder.dumpResources("testing/" + iAddonPack.getDisplayNameObsidian(), "assets");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}).start();
			});
		});
	}

}
