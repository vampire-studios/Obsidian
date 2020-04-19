package io.github.vampirestudios.obsidian;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.api.TextureInformation;
import io.github.vampirestudios.obsidian.api.block.Block;
import io.github.vampirestudios.obsidian.api.entity.Entity;
import io.github.vampirestudios.obsidian.api.item.Item;
import io.github.vampirestudios.obsidian.api.potion.Potion;
import io.github.vampirestudios.vampirelib.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ArtificeAssetGeneration implements ClientModInitializer {

    public static List<Item> items = new ArrayList<>();
    public static List<Block> blocks = new ArrayList<>();
    public static List<Entity> entities = new ArrayList<>();
    public static List<Potion> potions = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        Artifice.registerAssets("client_side_resources", clientResourcePackBuilder -> {
            for (Item item : items) {
                clientResourcePackBuilder.addTranslations(new Identifier(item.information.name.getNamespace(), "en_us"), translationBuilder ->
                        translationBuilder.entry(String.format("item.%s.%s", item.information.name.getNamespace(), item.information.name.getPath()),
                                item.information.displayName));
                if (item.information.texturesAndModels.textures != null) {
                    clientResourcePackBuilder.addItemModel(Utils.prependToPath(item.information.name, "item/"), modelBuilder -> {
                        modelBuilder.parent(item.information.texturesAndModels.modelType);
                        for(TextureInformation texture : item.information.texturesAndModels.textures) {
                            modelBuilder.texture(texture.textureName, texture.texturePath);
                        }
                    });
                }
            }
            for (Block block : blocks) {
                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.getNamespace(), "en_us"), translationBuilder ->
                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.getNamespace(), block.information.name.getPath()),
                                block.information.displayName));
                if (block.information.texturesAndModels.textures != null) {
                    clientResourcePackBuilder.addBlockState(Utils.prependToPath(block.information.name, "blockstates/"), modelBuilder -> {
                        modelBuilder.variant("", variant -> variant.model(Utils.prependToPath(block.information.name, "block/")));
                    });
                    clientResourcePackBuilder.addBlockModel(Utils.prependToPath(block.information.name, "block/"), modelBuilder -> {
                        modelBuilder.parent(block.information.texturesAndModels.modelType);
                        for(TextureInformation texture : block.information.texturesAndModels.textures) {
                            modelBuilder.texture(texture.textureName, texture.texturePath);
                        }
                    });
                    clientResourcePackBuilder.addItemModel(Utils.prependToPath(block.information.name, "item/"), modelBuilder -> {
                        modelBuilder.parent(Utils.prependToPath(block.information.name, "block/"));
                    });
                }
            }
        });
    }

}