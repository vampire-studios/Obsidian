package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceReloading {
    private static HashMap<Identifier, Block> blocks = new HashMap<>();
    private static HashMap<Identifier, Item> items = new HashMap<>();

    public static CompletableFuture<Void> resourceReload(ResourceReloadListener.Synchronizer stage, ResourceManager resourceManager, Profiler preparationsProfiler, Profiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        HashMap<Identifier, Block> tempBlocks = new HashMap<>();
        HashMap<Identifier, Item> tempItems = new HashMap<>();

        CompletableFuture[] blockFutures = resourceManager.findResources("blocks", fileName -> fileName.endsWith(".json"))
                .stream()
                .map(location -> CompletableFuture.supplyAsync(() -> location))
                .map(completable -> completable.thenAcceptAsync(resource -> tempBlocks.put(resource, loadBlock(resource, resourceManager))))
                .toArray(CompletableFuture[]::new);
        CompletableFuture[] itemFutures = resourceManager.findResources("items", fileName -> fileName.endsWith(".json"))
                .stream()
                .map(location -> CompletableFuture.supplyAsync(() -> location))
                .map(completable -> completable.thenAcceptAsync(resource -> tempItems.put(resource, loadItem(resource, resourceManager))))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(ArrayUtils.addAll(blockFutures, itemFutures)).thenAccept(x -> {
            blocks = tempBlocks;
            items = tempItems;
        }).thenCompose(stage::whenPrepared);
    }

    public static Block loadBlock(Identifier location, ResourceManager resourceManager) {
        try {
            return Obsidian.GSON.fromJson(getModelAsString(resourceManager, location), Block.class);
        } catch (Exception e) {
            Obsidian.LOGGER.error(String.format("Error parsing %S", location), e);
            throw (new RuntimeException(e));
        }
    }

    public static Item loadItem(Identifier location, ResourceManager resourceManager) {
        try {
            return Obsidian.GSON.fromJson(getModelAsString(resourceManager, location), Item.class);
        } catch (Exception e) {
            Obsidian.LOGGER.error(String.format("Error parsing %S", location), e);
            throw (new RuntimeException(e));
        }
    }

    private static String getModelAsString(ResourceManager resourceManager, Identifier location) {
        try (InputStream inputStream = getStreamForIdentifier(location)) {
            Obsidian.LOGGER.error(IOUtils.toString(inputStream));
            return IOUtils.toString(inputStream);
        } catch (Exception e) {
            String message = "Couldn't load " + location;
            Obsidian.LOGGER.error(message, e);
            throw new RuntimeException(new FileNotFoundException(location.toString()));
        }
    }

    public static InputStream getStreamForIdentifier(Identifier resourceLocation) {
        return new BufferedInputStream(Obsidian.class.getResourceAsStream("/content/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath()));
    }

    public HashMap<Identifier, Block> getBlocks() {
        return blocks;
    }

    public HashMap<Identifier, Item> getItems() {
        return items;
    }

}
