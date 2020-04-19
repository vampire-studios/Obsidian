package io.github.vampirestudios.obsidian;

import com.google.gson.*;
import io.github.vampirestudios.hidden_gems.HiddenGems;
import io.github.vampirestudios.obsidian.api.block.Block;
import io.github.vampirestudios.obsidian.api.entity.Entity;
import io.github.vampirestudios.obsidian.api.item.FoodItem;
import io.github.vampirestudios.obsidian.api.item.ToolItem;
import io.github.vampirestudios.obsidian.api.potion.Potion;
import io.github.vampirestudios.obsidian.api.world.Biome;
import io.github.vampirestudios.obsidian.api.world.Dimension;
import io.github.vampirestudios.obsidian.minecraft.BiomeImpl;
import io.github.vampirestudios.obsidian.minecraft.EntityImpl;
import io.github.vampirestudios.vampirelib.utils.registry.RegistryUtils;
import net.fabricmc.fabric.api.biomes.v1.NetherBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Deprecated
public class DataPackModdingManager implements SimpleResourceReloadListener<DataPackModdingManager.RawData> {

    @Override
    public CompletableFuture<RawData> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> new RawData(manager), executor);
    }

    @Override
    public CompletableFuture<Void> apply(RawData data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(data::apply, executor);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(HiddenGems.MOD_ID, "data_pack_modding");
    }

    public void registerReloadListener() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this);
    }

    public static class RawData {

        private static final Gson GSON = new GsonBuilder()
                .setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
                .setLenient()
                .create();

        List<FoodItem> foodItems = new ArrayList<>();
        List<ToolItem> toolItems = new ArrayList<>();
        List<Block> blocks = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();
        List<Potion> potions = new ArrayList<>();
        List<Dimension> dimensions = new ArrayList<>();
        List<Biome> biomes = new ArrayList<>();

        RawData(ResourceManager manager) {
            for (Identifier id : manager.findResources("config/data_pack_modding", path -> path.endsWith(".json"))) {
                try {
                    InputStreamReader reader = new InputStreamReader(manager.getResource(id).getInputStream());
                    if (id.getPath().startsWith("config/data_pack_modding/food/"))
                        foodItems.add(GSON.fromJson(reader, FoodItem.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/tools/"))
                        toolItems.add(GSON.fromJson(reader, ToolItem.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/blocks/"))
                        blocks.add(GSON.fromJson(reader, Block.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/entities/"))
                        entities.add(GSON.fromJson(reader, Entity.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/potions/"))
                        potions.add(GSON.fromJson(reader, Potion.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/biomes/"))
                        biomes.add(GSON.fromJson(reader, Biome.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/dimensions/"))
                        dimensions.add(GSON.fromJson(reader, Dimension.class));
                    else HiddenGems.LOGGER.warn("[HiddenGems:DataPackModdingManager] Unknown resource '{}'", id);
                } catch (JsonIOException | JsonSyntaxException ex) {
                    HiddenGems.LOGGER.error("[HiddenGems:DataPackModdingManager] Error while parsing resource '{}'", id, ex);
                } catch (IOException ex) {
                    HiddenGems.LOGGER.error("[HiddenGems:DataPackModdingManager] Error reading resource '{}'", id, ex);
                } catch (Exception ex) {
                    HiddenGems.LOGGER.error("[HiddenGems:DataPackModdingManager] Error loading resource '{}'", id, ex);
                }
            }
        }

        public void apply() {
            for(FoodItem foodItem : this.foodItems) {
                FoodComponent foodComponent = foodItem.components.food.getBuilder().build();
                Registry.register(Registry.ITEM, foodItem.information.name, new Item(new Item.Settings()
                        .group(foodItem.information.getItemGroup())
                        .maxCount(foodItem.information.getMaxCount())
                        .maxDamage(foodItem.components.use_duration)
                        .rarity(foodItem.information.getRarity())
                        .food(foodComponent)));
                System.out.println(String.format("Registered a food called %s", foodItem.information.name));
                ArtificeAssetGeneration.items.add(foodItem);
            }
            for(ToolItem toolItem : this.toolItems) {
                RegistryUtils.registerItem(new Item(new Item.Settings().group(toolItem.information.getItemGroup()).maxCount(toolItem.information.getMaxCount())
                        .rarity(toolItem.information.getRarity())), toolItem.information.name);
                System.out.println(String.format("Registered a tool called %s", toolItem.information.name));
                ArtificeAssetGeneration.items.add(toolItem);
            }
            for(Block block : this.blocks) {
                RegistryUtils.register(new net.minecraft.block.Block(net.minecraft.block.Block.Settings.of(block.information.getMaterial())),
                        block.information.name,
                        block.information.getItemGroup());
                System.out.println(String.format("Registered a block called %s", block.information.name));
                ArtificeAssetGeneration.blocks.add(block);
            }
            for(Entity entity : this.entities) {
                EntityType<EntityImpl> type = new EntityType<>(EntityImpl::new, entity.components.getCategory(),
                        false, entity.summonable, false, entity.spawnable, 0, 0,
                        EntityDimensions.fixed(entity.components.collision_box.width, entity.components.collision_box.height));
                Registry.register(Registry.ENTITY_TYPE, entity.identifier, type);
                if (entity.spawnable) {
                    Registry.register(Registry.ITEM, new Identifier(entity.identifier.getNamespace(), String.format("%s_spawn_egg", entity.identifier.getPath())),
                            new SpawnEggItem(type, entity.spawnEggColorMain, entity.spawnEggColorOverlay, new Item.Settings().group(ItemGroup.MISC)));
                }
                System.out.println(String.format("Registered an entity called %s", entity.identifier));
                ArtificeAssetGeneration.entities.add(entity);
            }
            for (Potion potion : this.potions) {
                if(potion == null) continue;
                    Registry.register(Registry.POTION, potion.name,
                            new net.minecraft.potion.Potion(new StatusEffectInstance(potion.getEffectType(), potion.getEffects().duration * 20, potion.getEffects().amplifier)));
                    System.out.println(String.format("Registered a potion effect called %s", potion.name));
                ArtificeAssetGeneration.potions.add(potion);
            }
            for (Biome biome : this.biomes) {
                if(biome == null) continue;
                BiomeImpl biome1 = new BiomeImpl(biome);
                Registry.register(Registry.BIOME, biome.name, biome1);
                if(biome.dimension.equals("overworld")) {
                    OverworldBiomes.addContinentalBiome(biome1, OverworldClimate.TEMPERATE, 1.0);
                }
                if(biome.dimension.equals("nether")) {
                    NetherBiomes.addNetherBiome(biome1);
                }
                System.out.println(String.format("Registered a biome called %s", biome.name));
            }
        }

    }

    @FunctionalInterface
    public interface SimpleStringDeserializer<T> extends Function<String, T>, JsonDeserializer<T> {
        default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return this.apply(json.getAsString());
        }
    }

}