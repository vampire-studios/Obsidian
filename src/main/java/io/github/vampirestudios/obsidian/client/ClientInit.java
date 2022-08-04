package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.artifice.api.builder.assets.TranslationBuilder;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.threadhandlers.assets.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientInit implements ClientModInitializer {

    /**
     * This is a map from the addon id to a map from the language id to a map from the translation key to the translation
     */
    private static final Map<String, Map<String, Map<String, String>>> translationMap = new HashMap<>();
    public static final List<ModelIdentifier> customModels = new ArrayList<>();

    public static void addTranslation(String addonId, String languageId, String translationKey, String translation) {
        synchronized (translationMap) {
            Map<String, Map<String, String>> addonTranslations = translationMap.computeIfAbsent(addonId, key -> new HashMap<>());
            Map<String, String> addonLanguageTranslations = addonTranslations.computeIfAbsent(languageId, key -> new HashMap<>());
            addonLanguageTranslations.put(translationKey, translation);
        }
    }

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Obsidian.SEAT, SeatEntityRenderer::new);
        ObsidianAddonLoader.OBSIDIAN_ADDONS.forEach(iAddonPack -> {
            String name = iAddonPack.getDisplayNameObsidian();
            Obsidian.registerAssetPack(new Identifier(iAddonPack.getConfigPackInfo().namespace, iAddonPack.getConfigPackInfo().namespace), clientResourcePackBuilder -> {
                if (!iAddonPack.getConfigPackInfo().hasAssets) {
                    clientResourcePackBuilder.setDisplayName(name);
                    clientResourcePackBuilder.shouldOverwrite();
				    for (Entity entity : ObsidianAddonLoader.ENTITIES)
				        if (entity.information.identifier.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
				            new EntityInitThread(clientResourcePackBuilder, entity).run();
                    for (ItemGroup itemGroup : ObsidianAddonLoader.ITEM_GROUPS)
                        if (itemGroup.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new ItemGroupInitThread(clientResourcePackBuilder, itemGroup).run();
                    for (Block block : ObsidianAddonLoader.BLOCKS)
                        if (block.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new BlockInitThread(clientResourcePackBuilder, block).run();
                    for (Block block : ObsidianAddonLoader.ORES)
                        if (block.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new BlockInitThread(clientResourcePackBuilder, block).run();
                    for (Item item : ObsidianAddonLoader.ITEMS)
                        if (item.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new ItemInitThread(clientResourcePackBuilder, item).run();
                    for (ArmorItem armor : ObsidianAddonLoader.ARMORS)
                        if (armor.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new ArmorInitThread(clientResourcePackBuilder, armor).run();
                    for (WeaponItem weapon : ObsidianAddonLoader.WEAPONS)
                        if (weapon.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new WeaponInitThread(clientResourcePackBuilder, weapon).run();
                    for (ToolItem tool : ObsidianAddonLoader.TOOLS)
                        if (tool.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new ToolInitThread(clientResourcePackBuilder, tool).run();
                    for (FoodItem foodItem : ObsidianAddonLoader.FOODS)
                        if (foodItem.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new FoodInitThread(clientResourcePackBuilder, foodItem).run();
                    for (Enchantment enchantment : ObsidianAddonLoader.ENCHANTMENTS)
                        if (enchantment.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new EnchantmentInitThread(enchantment).run();
                    for (ShieldItem shield : ObsidianAddonLoader.SHIELDS)
                        if (shield.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new ShieldInitThread(clientResourcePackBuilder, shield).run();
                    for (Elytra elytra : ObsidianAddonLoader.ELYTRAS)
                        if (elytra.information.name.id.getNamespace().equals(iAddonPack.getConfigPackInfo().namespace))
                            new ElytraInitThread(clientResourcePackBuilder, elytra).run();
                    translationMap.forEach((modId, modTranslations) -> modTranslations.forEach((languageId, translations) -> {
                        TranslationBuilder translationBuilder = new TranslationBuilder();
                        translations.forEach(translationBuilder::entry);
                        clientResourcePackBuilder.addTranslations(new Identifier(modId, languageId), translationBuilder);
                    }));
                    if (FabricLoader.getInstance().isDevelopmentEnvironment()) new Thread(() -> {
                        try {
                            if (FabricLoader.getInstance().isDevelopmentEnvironment())
                                clientResourcePackBuilder.dumpResources("testing/" + iAddonPack.getDisplayNameObsidian(), "assets");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            });
        });
    }

}
