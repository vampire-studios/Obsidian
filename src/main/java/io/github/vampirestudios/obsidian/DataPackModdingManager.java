package io.github.vampirestudios.obsidian;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import io.github.vampirestudios.obsidian.api.block.Block;
import io.github.vampirestudios.obsidian.api.command.Command;
import io.github.vampirestudios.obsidian.api.entity.Entity;
import io.github.vampirestudios.obsidian.api.item.FoodItem;
import io.github.vampirestudios.obsidian.api.item.ToolItem;
import io.github.vampirestudios.obsidian.api.potion.Potion;
import io.github.vampirestudios.obsidian.api.world.Biome;
import io.github.vampirestudios.obsidian.minecraft.EntityImpl;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
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
        return new Identifier(Obsidian.MOD_ID, "data_pack_modding");
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
        List<Biome> biomes = new ArrayList<>();
        List<Command> commands = new ArrayList<>();

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
                    else if (id.getPath().startsWith("config/data_pack_modding/commands/"))
                        commands.add(GSON.fromJson(reader, Command.class));
                    else Obsidian.LOGGER.warn("[Obsidian:DataPackModdingManager] Unknown resource '{}'", id);
                } catch (JsonIOException | JsonSyntaxException ex) {
                    Obsidian.LOGGER.error("[Obsidian:DataPackModdingManager] Error while parsing resource '{}'", id, ex);
                } catch (IOException ex) {
                    Obsidian.LOGGER.error("[Obsidian:DataPackModdingManager] Error reading resource '{}'", id, ex);
                } catch (Exception ex) {
                    Obsidian.LOGGER.error("[Obsidian:DataPackModdingManager] Error loading resource '{}'", id, ex);
                }
            }
        }

        public void apply() {
            for(FoodItem foodItem : this.foodItems) {
                FoodComponent foodComponent = foodItem.components.food.getBuilder().build();
                Registry.register(Registry.ITEM, foodItem.information.name, new Item(new Item.Settings()
                        .group(foodItem.information.getItemGroup())
                        .maxCount(foodItem.information.maxStackSize)
                        .maxDamage(foodItem.components.use_duration)
                        .rarity(foodItem.information.getRarity())
                        .food(foodComponent)));
                System.out.println(String.format("Registered a food called %s", foodItem.information.name));
                ArtificeAssetGeneration.items.add(foodItem);
            }
            for(ToolItem toolItem : this.toolItems) {
                RegistryUtils.registerItem(new Item(new Item.Settings().group(toolItem.information.getItemGroup()).maxCount(toolItem.information.maxStackSize)
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
                net.minecraft.entity.
                EntityType<EntityImpl> type = new EntityType<>(EntityImpl::new, entity.components.getCategory(),
                        false, entity.summonable, false, entity.spawnable, ImmutableSet.of(),
                        EntityDimensions.fixed(entity.components.collision_box.width, entity.components.collision_box.height), 0, 0);
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
        }

    }

    @FunctionalInterface
    public interface SimpleStringDeserializer<T> extends Function<String, T>, JsonDeserializer<T> {
        default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return this.apply(json.getAsString());
        }
    }

}