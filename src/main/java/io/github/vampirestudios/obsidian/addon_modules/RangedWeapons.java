package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.fabricmc.fabric.api.item.v1.bow.FabricBowItem;
import net.fabricmc.fabric.api.item.v1.crossbow.SimpleCrossbowItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class RangedWeapons implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        RangedWeaponItem rangedWeapon = Obsidian.GSON.fromJson(new FileReader(file), RangedWeaponItem.class);
        try {
            if (rangedWeapon == null) return;
            Item.Settings settings = new Item.Settings().group(rangedWeapon.information.getItemGroup())
                    .maxCount(rangedWeapon.information.maxStackSize).rarity(rangedWeapon.information.rarity);
            switch (rangedWeapon.weapon_type) {
                case "bow" -> {
                    Item item = RegistryUtils.registerItem(new FabricBowItem(rangedWeapon, settings), rangedWeapon.information.name.id);
                    ModelPredicateProviderRegistry.register(item, new Identifier("pull"), (stack, world, entity, seed) -> {
                        if (entity == null) return 0.0F;
                        else return entity.getActiveItem() != stack ? 0.0F : (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0F;
                    });
                    ModelPredicateProviderRegistry.register(item, new Identifier("pulling"), (stack, world, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
                }
                case "crossbow" ->  {
                    Item item = RegistryUtils.registerItem(new SimpleCrossbowItem(rangedWeapon, settings), rangedWeapon.information.name.id);
                    ModelPredicateProviderRegistry.register(item, new Identifier("pull"), (stack, world, entity, seed) -> {
                        if (entity == null) {
                            return 0.0F;
                        } else {
                            return CrossbowItem.isCharged(stack) ? 0.0F : (float)(stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / (float)CrossbowItem.getPullTime(stack);
                        }
                    });
                    ModelPredicateProviderRegistry.register(item, new Identifier("pulling"), (stack, world, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getActiveItem() == stack &&
                                    !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
                    ModelPredicateProviderRegistry.register(item, new Identifier("charged"), (stack, world, entity, seed) ->
                            entity != null && CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
                    ModelPredicateProviderRegistry.register(item, new Identifier("firework"), (stack, world, entity, seed) ->
                            entity != null && CrossbowItem.isCharged(stack) && CrossbowItem.hasProjectile(stack,
                                    Items.FIREWORK_ROCKET) ? 1.0F : 0.0F);
                }
                /*case "trident" -> {
                    Item item = RegistryUtils.registerItem(new TridentItemImpl(rangedWeapon, settings), rangedWeapon.information.name.id);
                    FabricModelPredicateProviderRegistry.register(item, new Identifier("throwing"), (stack, world, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
                }*/
            }
            register(ContentRegistries.RANGED_WEAPONS, "ranged_weapon", rangedWeapon.information.name.id, rangedWeapon);
        } catch (Exception e) {
            failedRegistering("ranged_weapon", rangedWeapon.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/weapons/ranged";
    }
}
