package io.github.vampirestudios.obsidian.client;

import com.raphydaphy.breakoutapi.BreakoutAPI;
import com.raphydaphy.breakoutapi.BreakoutAPIClient;
import com.raphydaphy.breakoutapi.demo.integrated.DemoIntegratedBreakout;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.breakout.BlockBreakout;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.obsidian.threadhandlers.assets.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.Random;

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

        ClientPlayNetworking.registerGlobalReceiver(Obsidian.GENERATOR_BREAKOUT_PACKET, (client, handler, buf, responseSender) -> {
            String type = buf.readString();
            client.execute(() -> {
                String randomId = "" + (new Random()).nextDouble();
                Identifier id;
                switch (type) {
                    case "block":
                        id = new Identifier(BreakoutAPI.MODID, "block-" + randomId);
                        BreakoutAPIClient.openBreakout(id, new BlockBreakout(id));
                        break;
                    case "item":
                        id = new Identifier(BreakoutAPI.MODID, "item-" + randomId);
                        BreakoutAPIClient.openBreakout(id, new DemoIntegratedBreakout(id));
                        break;
                    case "entity":
                        id = new Identifier(BreakoutAPI.MODID, "entity-" + randomId);
                        BreakoutAPIClient.openBreakout(id, new DemoIntegratedBreakout(id));
                        break;
                    case "cauldron_type":
                        id = new Identifier(BreakoutAPI.MODID, "cauldron_type-" + randomId);
                        BreakoutAPIClient.openBreakout(id, new DemoIntegratedBreakout(id));
                        break;
                    case "enchantments":
                        id = new Identifier(BreakoutAPI.MODID, "enchantments-" + randomId);
                        BreakoutAPIClient.openBreakout(id, new DemoIntegratedBreakout(id));
                        break;
                    case "potion":
                        id = new Identifier(BreakoutAPI.MODID, "potion-" + randomId);
                        BreakoutAPIClient.openBreakout(id, new DemoIntegratedBreakout(id));
                        break;
                    case "villager_profession":
                        id = new Identifier(BreakoutAPI.MODID, "villager_profession-" + randomId);
                        BreakoutAPIClient.openBreakout(id, new DemoIntegratedBreakout(id));
                        break;
                    case "villager_biome_type":
                        id = new Identifier(BreakoutAPI.MODID, "villager_biome_type-" + randomId);
                        BreakoutAPIClient.openBreakout(id, new DemoIntegratedBreakout(id));
                        break;
                    case "status_effects":
                        id = new Identifier(BreakoutAPI.MODID, "status_effects-" + randomId);
                        BreakoutAPIClient.openBreakout(id, new DemoIntegratedBreakout(id));
                        break;
                    default:
                        BreakoutAPI.LOGGER.warn("Tried to trigger invalid breakout type '" + type + "'");
                        break;
                }

            });
        });
    }

}
