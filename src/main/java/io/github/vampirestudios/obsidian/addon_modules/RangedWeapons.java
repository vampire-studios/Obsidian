package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BowItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CrossbowItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class RangedWeapons implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        RangedWeaponItem rangedWeapon = Obsidian.GSON.fromJson(new FileReader(file), RangedWeaponItem.class);
        try {
            if (rangedWeapon == null) return;
            Item.Properties settings = new Item.Properties().stacksTo(rangedWeapon.information.maxStackSize)
                    .rarity(Rarity.valueOf(rangedWeapon.information.rarity.toUpperCase(Locale.ROOT)));
            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    rangedWeapon.information.name.id,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (rangedWeapon.information.name.id == null) rangedWeapon.information.name.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
            Item item = null;
            switch (rangedWeapon.weapon_type) {
                case "bow" -> {
                    item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new BowItemImpl(rangedWeapon, settings));
                    ItemProperties.register(item, new ResourceLocation("pull"), (stack, world, entity, seed) -> {
                        if (entity == null) return 0.0F;
                        else return entity.getUseItem() != stack ? 0.0F : (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                    });
                    ItemProperties.register(item, new ResourceLocation("pulling"), (stack, world, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
                }
                case "crossbow" ->  {
                    item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new CrossbowItemImpl(rangedWeapon, settings));
                    ItemProperties.register(item, new ResourceLocation("pull"), (stack, world, entity, seed) -> {
                        if (entity == null) {
                            return 0.0F;
                        } else {
                            return CrossbowItem.isCharged(stack) ? 0.0F : (float)(stack.getUseDuration() - entity.getUseItemRemainingTicks()) / (float)CrossbowItem.getChargeDuration(stack);
                        }
                    });
                    ItemProperties.register(item, new ResourceLocation("pulling"), (stack, world, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack &&
                                    !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
                    ItemProperties.register(item, new ResourceLocation("charged"), (stack, world, entity, seed) ->
                            entity != null && CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
                    ItemProperties.register(item, new ResourceLocation("firework"), (stack, world, entity, seed) ->
                            entity != null && CrossbowItem.isCharged(stack) && CrossbowItem.containsChargedProjectile(stack,
                                    Items.FIREWORK_ROCKET) ? 1.0F : 0.0F);
                }
                /*case "trident" -> {
                    Item item = RegistryUtils.registerItem(new TridentItemImpl(rangedWeapon, settings), identifier);
                    FabricModelPredicateProviderRegistry.register(item, new Identifier("throwing"), (stack, world, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
                }*/
            }
            Item finalItem = item;
            ItemGroupEvents.modifyEntriesEvent(rangedWeapon.information.getItemGroup()).register(entries -> entries.accept(finalItem));
            register(ContentRegistries.RANGED_WEAPONS, "ranged_weapon", identifier, rangedWeapon);
        } catch (Exception e) {
            failedRegistering("ranged_weapon", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "items/weapons/ranged";
    }
}
