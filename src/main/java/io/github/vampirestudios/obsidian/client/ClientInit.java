package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.threadhandlers.assets.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
        EntityRendererRegistry.INSTANCE.register(Obsidian.SEAT, SeatEntityRenderer::new);
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
                    translationMap.forEach((modId, modTranslations) -> modTranslations.forEach((languageId, translations) ->
							clientResourcePackBuilder.addTranslations(new Identifier(modId, languageId),
									translationBuilder -> translations.forEach(translationBuilder::entry))
					));
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

    public static void setupFluidRendering(final Fluid still, final Fluid flowing, final Identifier textureFluidId, final int color) {
        final Identifier stillSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_still");
        final Identifier flowingSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_flow");

        // If they're not already present, add the sprites to the block atlas
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(stillSpriteId);
            registry.register(flowingSpriteId);
        });

        final Identifier fluidId = Registry.FLUID.getId(still);
        final Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

        final Sprite[] fluidSprites = { null, null };

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return listenerId;
            }

            /**
             * Get the sprites from the block atlas when resources are reloaded
             */
            @Override
            public void reload(ResourceManager resourceManager) {
                final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                fluidSprites[0] = atlas.apply(stillSpriteId);
                fluidSprites[1] = atlas.apply(flowingSpriteId);
            }
        });

        // The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
        final FluidRenderHandler renderHandler = new FluidRenderHandler()
        {
            @Override
            public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
                return fluidSprites;
            }

            @Override
            public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
                return color;
            }
        };

        FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
    }

}
