package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.SubItemGroup;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.api.obsidian.ui.GUI;
import io.github.vampirestudios.obsidian.client.renderer.SeatEntityRenderer;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.minecraft.DynamicContainer;
import io.github.vampirestudios.obsidian.minecraft.JsonGui;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.threadhandlers.assets_temp.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.SimpleContainer;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.api.SidedRRPCallback;
import net.vampirestudios.arrp.assets.lang.Lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClientInit implements ClientModInitializer {

    /**
     * This is a map from the addon id to a map from the language id to a map from the translation key to the translation.<br>
     * <code>Map< AddonID, Map< LanguageID, Map< TranslationKey, Translation > > ></code>
     */
    public static final Map<String, Map<String, Map<String, String>>> translationMap = new HashMap<>();

    public static void addTranslation( String addonId, String languageId, String translationKey, String translation ) {
        if (addonId == null || languageId == null || translationKey == null || translation == null) return;
        synchronized (translationMap) {
            Map<String, Map<String, String>> addonTranslations = translationMap.computeIfAbsent( addonId, _ -> new HashMap<>() );
            Map<String, String> addonLanguageTranslations = addonTranslations.computeIfAbsent( languageId, _ -> new HashMap<>() );
            addonLanguageTranslations.put( translationKey, translation );
        }
    }

    @Override
    public void onInitializeClient() {
        Obsidian.LOGGER.info("You're now running Obsidian v{} on client-side for {}", Const.MOD_VERSION, SharedConstants.getCurrentVersion().name());

//        ContentPackSyncNetworking.registerClientReceivers();

        EntityRenderers.register(Obsidian.SEAT, SeatEntityRenderer::new);
        EntityRenderers.register(Obsidian.THROWN_KNIFE, ThrownItemRenderer::new);
        ObsidianAddonLoader.OBSIDIAN_ADDONS.forEach(iAddonPack -> {
            String id;
            if (iAddonPack.getConfigPackInfo() instanceof LegacyObsidianAddonInfo legacyObsidianAddonInfo) {
                id = legacyObsidianAddonInfo.namespace;
            } else {
                ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) iAddonPack.getConfigPackInfo();
                id = addonInfo.addon.id;
            }

            ClientCommandRegistrationCallback.EVENT.register((dispatcher, _) -> dispatcher.register(
                ClientCommands.literal("opengui").then(
                    ClientCommands.argument("gui", IdentifierArgument.id())
                        .suggests(new GuiSuggestionProvider())
                        .executes(context -> {
                            Identifier gui = context.getArgument("gui", Identifier.class);
                            if (ContentRegistries.GUIS.containsKey(gui)) {
                                FabricClientCommandSource commandSource = context.getSource();
                                GUI gui1 = ContentRegistries.GUIS.getValue(gui);
                                DynamicContainer dynamicContainer = new DynamicContainer(0, commandSource.getPlayer().getInventory());
                                dynamicContainer.setGui(gui1);
                                dynamicContainer.setContainerInventory(new SimpleContainer(Objects.requireNonNull(gui1).containerSize));
                                dynamicContainer.setupSlots();
                                Minecraft.getInstance().gui.setScreen(new JsonGui(dynamicContainer, commandSource.getPlayer().getInventory(),
                                        Objects.requireNonNull(gui1)
                                ));
                                return 1;
                            } else return 0;
                        })
                )
            ));

            if (!iAddonPack.getConfigPackInfo().hasAssets) {
                initializeOneTimeClientContent(id);
                boolean[] firstGeneration = {true};
                SidedRRPCallback.AFTER_VANILLA.register((type, packs) -> {
                    if (type != PackType.CLIENT_RESOURCES) {
                        return;
                    }
                    RuntimeResourcePack resourcePack = iAddonPack.getResourcePack();
                    generateAssets(id, resourcePack, firstGeneration[0]);
                    if (firstGeneration[0]) {
                        firstGeneration[0] = false;
                    }
                    packs.add(resourcePack);
                });
            }
        });
    }

    private static void generateAssets(String id, RuntimeResourcePack resourcePack, boolean registerBlockColors) {
        for (Block block : ContentRegistries.BLOCKS)
            if (block.information.id.getNamespace().equals(id))
                new BlockInitThread(resourcePack, block, registerBlockColors).run();
        for (Block block : ContentRegistries.ORES)
            if (block.information.id.getNamespace().equals(id))
                new BlockInitThread(resourcePack, block, registerBlockColors).run();
        for (Item item : ContentRegistries.ITEMS)
            if (item.information.id.getNamespace().equals(id))
                new ItemInitThread(resourcePack, item).run();
        for (NexoItem item : ContentRegistries.NEXO_ITEMS)
            if (item.id.getNamespace().equals(id))
                new OraxenItemInitThread(resourcePack, item).run();
        for (ToolItem item : ContentRegistries.TOOLS)
            if (item.information.id.getNamespace().equals(id))
                new ItemInitThread(resourcePack, item).run();
        for (SoundPlayingItem item : ContentRegistries.SOUND_PLAYING_ITEMS)
            if (item.information.id.getNamespace().equals(id))
                new ItemInitThread(resourcePack, item).run();
        for (WeaponItem item : ContentRegistries.WEAPONS)
            if (item.information.id.getNamespace().equals(id))
                new ItemInitThread(resourcePack, item).run();
        for (RangedWeaponItem item : ContentRegistries.RANGED_WEAPONS)
            if (item.information.id.getNamespace().equals(id))
                new ItemInitThread(resourcePack, item).run();
        for (ShieldItem item : ContentRegistries.SHIELDS)
            if (item.information.id.getNamespace().equals(id))
                new ItemInitThread(resourcePack, item).run();
        for (FoodItem foodItem : ContentRegistries.FOODS)
            if (foodItem.information.id.getNamespace().equals(id))
                new ItemInitThread(resourcePack, foodItem).run();
        for (ArmorItem armor : ContentRegistries.ARMORS)
            if (armor.information.id.getNamespace().equals(id))
                new ArmorInitThread(resourcePack, armor).run();
        translationMap.forEach((modId, modTranslations) -> modTranslations.forEach((languageId, translations) -> {
            Lang lang = Lang.lang();
            translations.forEach(lang::entry);
            resourcePack.addLang(Identifier.fromNamespaceAndPath(modId, languageId), lang);
        }));
    }

    private static void initializeOneTimeClientContent(String id) {
        for (Entity entity : ContentRegistries.ENTITIES)
            if (entity.description.id != null && entity.description.id.getNamespace().equals(id))
                new EntityInitThread(entity).run();
        for (ItemGroup itemGroup : ContentRegistries.ITEM_GROUPS)
            if (itemGroup.id.getNamespace().equals(id))
                new ItemGroupInitThread(itemGroup).run();
        for (SubItemGroup itemGroup : Registries.SUB_ITEM_GROUPS)
            if (itemGroup.id.getNamespace().equals(id))
                new SubItemGroupInitThread(itemGroup).run();
        for (Elytra elytra : ContentRegistries.ELYTRAS)
            if (elytra.information.id.getNamespace().equals(id))
                new ElytraInitThread(elytra).run();
    }

}
