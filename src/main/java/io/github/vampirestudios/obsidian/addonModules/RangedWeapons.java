package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.SimpleTridentItem;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.fabricmc.fabric.api.item.v1.crossbow.SimpleCrossbowItem;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class RangedWeapons implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        RangedWeaponItem rangedWeapon = Obsidian.GSON.fromJson(new FileReader(file), RangedWeaponItem.class);
        try {
            if (rangedWeapon == null) return;
            Item.Settings settings = new Item.Settings().group(rangedWeapon.information.getItemGroup())
                    .maxCount(rangedWeapon.information.max_count).rarity(rangedWeapon.information.getRarity());
            switch (rangedWeapon.weapon_type) {
                /*case "bow" -> {
                    Item item = RegistryUtils.registerItem(new FabricBowItem(rangedWeapon, settings), rangedWeapon.information.name.id);
                    FabricModelPredicateProviderRegistry.register(item, new Identifier("pull"), (stack, world, entity, seed) -> {
                        if (entity == null) {
                            return 0.0F;
                        } else {
                            return entity.getActiveItem() != stack ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                        }
                    });
                    FabricModelPredicateProviderRegistry.register(item, new Identifier("pulling"), (stack, world, entity, seed) -> {
                        return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
                    });
                }*/
                case "crossbow" ->  {
                    Item item = RegistryUtils.registerItem(new SimpleCrossbowItem(rangedWeapon, settings), rangedWeapon.information.name.id);
                    FabricModelPredicateProviderRegistry.register(item, new Identifier("pull"), (stack, world, entity, seed) -> {
                        if (entity == null) {
                            return 0.0F;
                        } else {
                            return CrossbowItem.isCharged(stack) ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / (float)CrossbowItem.getPullTime(stack);
                        }
                    });
                    FabricModelPredicateProviderRegistry.register(item, new Identifier("pulling"), (stack, world, entity, seed) -> {
                        return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
                    });
                    FabricModelPredicateProviderRegistry.register(item, new Identifier("charged"), (stack, world, entity, seed) -> {
                        return entity != null && CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
                    });
                    FabricModelPredicateProviderRegistry.register(item, new Identifier("firework"), (stack, world, entity, seed) -> {
                        return entity != null && CrossbowItem.isCharged(stack) && CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
                    });
                }
                case "trident" -> {
                    Item item = RegistryUtils.registerItem(new SimpleTridentItem(rangedWeapon, settings), rangedWeapon.information.name.id);
                    FabricModelPredicateProviderRegistry.register(item, new Identifier("throwing"), (stack, world, entity, seed) -> {
                        return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F;
                    });
                }
            }
            register(RANGED_WEAPONS, "ranged_weapon", rangedWeapon.information.name.id, rangedWeapon);
        } catch (Exception e) {
            failedRegistering("ranged_weapon", rangedWeapon.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/weapons/ranged";
    }
}
