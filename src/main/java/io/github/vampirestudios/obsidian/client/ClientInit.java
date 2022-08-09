package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.artifice.api.builder.assets.TranslationBuilder;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.client.renderer.SeatEntityRenderer;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.threadhandlers.assets.*;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import java.io.IOException;
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
    public static final List<ModelIdentifier> customModels = new ArrayList<>();

    public static void addTranslation( String addonId, String languageId, String translationKey, String translation ) {
        synchronized (translationMap) {
            Map<String, Map<String, String>> addonTranslations = translationMap.computeIfAbsent( addonId, key -> new HashMap<>() );
            Map<String, String> addonLanguageTranslations = addonTranslations.computeIfAbsent( languageId, key -> new HashMap<>() );
            addonLanguageTranslations.put( translationKey, translation );
        }
    }

    @Override
    public void onInitializeClient(ModContainer mod) {
        EntityRendererRegistry.register(Obsidian.SEAT, SeatEntityRenderer::new);
        for (var addonPack : ObsidianAddonLoader.OBSIDIAN_ADDONS) {
            var name = addonPack.getObsidianDisplayName();
            String namespace;
            if (addonPack.getConfigPackInfo() instanceof LegacyObsidianAddonInfo legacyObsidianAddonInfo) {
                namespace = legacyObsidianAddonInfo.namespace;
            } else {
                ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addonPack.getConfigPackInfo();
                namespace = addonInfo.addon.id;
            }
            Obsidian.registerAssetPack(
                    new Identifier(namespace, namespace),
                    builder -> {
                        if (addonPack.getConfigPackInfo().hasAssets) return;
                        builder.setDisplayName(Text.literal(name));
                        builder.shouldOverwrite();
                    }
            );

        }
        ObsidianAddonLoader.OBSIDIAN_ADDONS.forEach(iAddonPack -> {
            String name = iAddonPack.getObsidianDisplayName();
            String id;
            if (iAddonPack.getConfigPackInfo() instanceof LegacyObsidianAddonInfo legacyObsidianAddonInfo) {
                id = legacyObsidianAddonInfo.namespace;
            } else {
                ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) iAddonPack.getConfigPackInfo();
                id = addonInfo.addon.id;
            }
            Obsidian.registerAssetPack(new Identifier(id, id), clientResourcePackBuilder -> {
                if (!iAddonPack.getConfigPackInfo().hasAssets) {
                    clientResourcePackBuilder.setDisplayName(Text.literal(name));
                    clientResourcePackBuilder.shouldOverwrite();
				    for (Entity entity : ContentRegistries.ENTITIES)
				        if (entity.information.identifier.getNamespace().equals(id))
				            new EntityInitThread(clientResourcePackBuilder, entity).run();
                    for (ItemGroup itemGroup : ContentRegistries.ITEM_GROUPS)
                        if (itemGroup.name.id.getNamespace().equals(id))
                            new ItemGroupInitThread(clientResourcePackBuilder, itemGroup).run();
                    for (Block block : ContentRegistries.BLOCKS)
                        if (block.information.name.id.getNamespace().equals(id))
                            new BlockInitThread(clientResourcePackBuilder, block).run();
                    for (Block block : ContentRegistries.ORES)
                        if (block.information.name.id.getNamespace().equals(id))
                            new BlockInitThread(clientResourcePackBuilder, block).run();
                    for (Item item : ContentRegistries.ITEMS)
                        if (item.information.name.id.getNamespace().equals(id))
                            new ItemInitThread(clientResourcePackBuilder, item).run();
                    for (ArmorItem armor : ContentRegistries.ARMORS)
                        if (armor.information.name.id.getNamespace().equals(id))
                            new ArmorInitThread(clientResourcePackBuilder, armor).run();
                    for (WeaponItem weapon : ContentRegistries.WEAPONS)
                        if (weapon.information.name.id.getNamespace().equals(id))
                            new WeaponInitThread(clientResourcePackBuilder, weapon).run();
                    for (ToolItem tool : ContentRegistries.TOOLS)
                        if (tool.information.name.id.getNamespace().equals(id))
                            new ToolInitThread(clientResourcePackBuilder, tool).run();
                    for (FoodItem foodItem : ContentRegistries.FOODS)
                        if (foodItem.information.name.id.getNamespace().equals(id))
                            new FoodInitThread(clientResourcePackBuilder, foodItem).run();
                    for (Enchantment enchantment : ContentRegistries.ENCHANTMENTS)
                        if (enchantment.name.id.getNamespace().equals(id))
                            new EnchantmentInitThread(enchantment).run();
                    for (ShieldItem shield : ContentRegistries.SHIELDS)
                        if (shield.information.name.id.getNamespace().equals(id))
                            new ShieldInitThread(clientResourcePackBuilder, shield).run();
                    for (Elytra elytra : ContentRegistries.ELYTRAS)
                        if (elytra.information.name.id.getNamespace().equals(id))
                            new ElytraInitThread(clientResourcePackBuilder, elytra).run();
                    translationMap.forEach((modId, modTranslations) -> modTranslations.forEach((languageId, translations) -> {
                        TranslationBuilder translationBuilder = new TranslationBuilder();
                        translations.forEach(translationBuilder::entry);
                        clientResourcePackBuilder.addTranslations(new Identifier(modId, languageId), translationBuilder);
                    }));
                    if (QuiltLoader.isDevelopmentEnvironment()) new Thread(() -> {
                        try {
                            clientResourcePackBuilder.dumpResources("testing/" + iAddonPack.getObsidianDisplayName(), "assets");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            });
        });
    }

}
