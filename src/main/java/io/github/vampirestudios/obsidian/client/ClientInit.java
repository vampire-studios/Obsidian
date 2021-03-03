package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.obsidian.threadhandlers.*;
import net.fabricmc.api.ClientModInitializer;
public class ClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for (Entity entity : ConfigHelper.ENTITIES) new EntityInitThread(entity).run();
        for (ItemGroup itemGroup : ConfigHelper.ITEM_GROUPS) new ItemGroupInitThread(itemGroup).run();
        for (Block block : ConfigHelper.BLOCKS) new BlockInitThread(block).run();
        for (Block block : ConfigHelper.ORES) new BlockInitThread(block).run();
        for (Item item : ConfigHelper.ITEMS) new ItemInitThread(item).run();
        for (ArmorItem armor : ConfigHelper.ARMORS) new ArmorInitThread(armor).run();
        for (WeaponItem weapon : ConfigHelper.WEAPONS) new WeaponInitThread(weapon).run();
        for (ToolItem tool : ConfigHelper.TOOLS) new ToolInitThread(tool).run();
        for (FoodItem foodItem : ConfigHelper.FOODS) new FoodInitThread(foodItem).run();
        for (Enchantment enchantment : ConfigHelper.ENCHANTMENTS) new EnchantmentInitThread(enchantment).run();
        for (ShieldItem shield : ConfigHelper.SHIELDS) new ShieldInitThread(shield).run();
        for (Elytra elytra : ConfigHelper.ELYTRAS) new ElytraInitThread(elytra).run();
    }

}
