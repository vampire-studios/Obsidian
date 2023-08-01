package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.SubItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.client.renderer.SeatEntityRenderer;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.threadhandlers.assets_temp.*;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientInit implements ClientModInitializer {

    /**
     * This is a map from the addon id to a map from the language id to a map from the translation key to the translation.<br>
     * <code>Map< AddonID, Map< LanguageID, Map< TranslationKey, Translation > > ></code>
     */
    private static final Map<String, Map<String, Map<String, String>>> translationMap = new HashMap<>();
    public static final List<ModelResourceLocation> customModels = new ArrayList<>();

    public static void addTranslation( String addonId, String languageId, String translationKey, String translation ) {
        synchronized (translationMap) {
            Map<String, Map<String, String>> addonTranslations = translationMap.computeIfAbsent( addonId, key -> new HashMap<>() );
            Map<String, String> addonLanguageTranslations = addonTranslations.computeIfAbsent( languageId, key -> new HashMap<>() );
            addonLanguageTranslations.put( translationKey, translation );
        }
    }

    @Override
    public void onInitializeClient() {
        Obsidian.LOGGER.info(String.format("You're now running Obsidian v%s on client-side for %s", Const.MOD_VERSION, SharedConstants.getCurrentVersion().getName()));

        EntityRendererRegistry.register(Obsidian.SEAT, SeatEntityRenderer::new);
        ObsidianAddonLoader.OBSIDIAN_ADDONS.forEach(iAddonPack -> {
            String id;
            if (iAddonPack.getConfigPackInfo() instanceof LegacyObsidianAddonInfo legacyObsidianAddonInfo) {
                id = legacyObsidianAddonInfo.namespace;
            } else {
                ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) iAddonPack.getConfigPackInfo();
                id = addonInfo.addon.id;
            }
            RuntimeResourcePack resourcePack = iAddonPack.getResourcePack();
            if (!iAddonPack.getConfigPackInfo().hasAssets) {
                for (Block block : ContentRegistries.BLOCKS)
                    if (block.information.name.id.getNamespace().equals(id))
                        new BlockInitThread(resourcePack, block).run();
                for (Block block : ContentRegistries.ORES)
                    if (block.information.name.id.getNamespace().equals(id))
                        new BlockInitThread(resourcePack, block).run();
                for (Item item : ContentRegistries.ITEMS)
                    if (item.information.name.id.getNamespace().equals(id))
                        new ItemInitThread(resourcePack, item).run();
                for (ToolItem item : ContentRegistries.TOOLS)
                    if (item.information.name.id.getNamespace().equals(id))
                        new ItemInitThread(resourcePack, item).run();
                for (WeaponItem item : ContentRegistries.WEAPONS)
                    if (item.information.name.id.getNamespace().equals(id))
                        new ItemInitThread(resourcePack, item).run();
                for (ShieldItem item : ContentRegistries.SHIELDS)
                    if (item.information.name.id.getNamespace().equals(id))
                        new ItemInitThread(resourcePack, item).run();
                for (FoodItem foodItem : ContentRegistries.FOODS)
                    if (foodItem.information.name.id.getNamespace().equals(id))
                        new ItemInitThread(resourcePack, foodItem).run();
                for (ArmorItem armor : ContentRegistries.ARMORS)
                    if (armor.information.name.id.getNamespace().equals(id))
                        new ArmorInitThread(resourcePack, armor).run();
                for (Enchantment enchantment : ContentRegistries.ENCHANTMENTS)
                    if (enchantment.name.id.getNamespace().equals(id))
                        new EnchantmentInitThread(enchantment).run();
                for (ItemGroup itemGroup : ContentRegistries.ITEM_GROUPS)
                    if (itemGroup.name.id.getNamespace().equals(id))
                        new ItemGroupInitThread(itemGroup).run();
                for (SubItemGroup itemGroup : Registries.SUB_ITEM_GROUPS)
                    if (itemGroup.name.id.getNamespace().equals(id))
                        new SubItemGroupInitThread(itemGroup).run();
                for (Elytra elytra : ContentRegistries.ELYTRAS)
                    if (elytra.information.name.id.getNamespace().equals(id))
                        new ElytraInitThread(elytra).run();
                translationMap.forEach((modId, modTranslations) -> modTranslations.forEach((languageId, translations) -> {
                    JLang lang = JLang.lang();
                    translations.forEach(lang::entry);
                    resourcePack.addLang(new ResourceLocation(modId, languageId), lang);
                }));
                RRPCallback.AFTER_VANILLA.register(a -> a.add(resourcePack));
                resourcePack.dump();
            }
        });
    }

}
