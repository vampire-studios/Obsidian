package io.github.vampirestudios.obsidian;

import com.google.gson.*;
import io.github.vampirestudios.obsidian.api.block.Block;
import io.github.vampirestudios.obsidian.api.command.Command;
import io.github.vampirestudios.obsidian.api.currency.Currency;
import io.github.vampirestudios.obsidian.api.entity.Entity;
import io.github.vampirestudios.obsidian.api.item.FoodItem;
import io.github.vampirestudios.obsidian.api.item.ToolItem;
import io.github.vampirestudios.obsidian.api.particle.Particle;
import io.github.vampirestudios.obsidian.api.potion.Potion;
import io.github.vampirestudios.obsidian.api.world.Biome;
import io.github.vampirestudios.obsidian.minecraft.*;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.getState;

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
        List<io.github.vampirestudios.obsidian.api.item.Item> items = new ArrayList<>();
        List<Particle> particles = new ArrayList<>();
        List<Currency> currencies = new ArrayList<>();

        RawData(ResourceManager manager) {
            for (Identifier id : manager.findResources("config/data_pack_modding", path -> path.endsWith(".json"))) {
                try {
                    InputStreamReader reader = new InputStreamReader(manager.getResource(id).getInputStream());
                    if (id.getPath().startsWith("config/data_pack_modding/items/food/"))
                        foodItems.add(GSON.fromJson(reader, FoodItem.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/items/"))
                        items.add(GSON.fromJson(reader, io.github.vampirestudios.obsidian.api.item.Item.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/items/tools/"))
                        toolItems.add(GSON.fromJson(reader, ToolItem.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/blocks/"))
                        blocks.add(GSON.fromJson(reader, Block.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/particles/"))
                        particles.add(GSON.fromJson(reader, Particle.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/entities/"))
                        entities.add(GSON.fromJson(reader, Entity.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/potions/"))
                        potions.add(GSON.fromJson(reader, Potion.class));
//                    else if (id.getPath().startsWith("config/data_pack_modding/biomes/"))
//                        biomes.add(GSON.fromJson(reader, Biome.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/commands/"))
                        commands.add(GSON.fromJson(reader, Command.class));
                    else if (id.getPath().startsWith("config/data_pack_modding/currency/"))
                        currencies.add(GSON.fromJson(reader, Currency.class));
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
                FoodComponent foodComponent = foodItem.food_information.getBuilder().build();
                Registry.register(Registry.ITEM, foodItem.information.name.id, new ItemImpl(foodItem, new Item.Settings()
                        .group(foodItem.information.getItemGroup())
                        .maxCount(foodItem.information.max_count)
                        .maxDamage(foodItem.information.use_duration)
                        .food(foodComponent)));
                System.out.println(String.format("Registered a food called %s", foodItem.information.name));
                ArtificeAssetGeneration.items.add(foodItem);
            }
            for(ToolItem tool : this.toolItems) {
                ToolMaterial material = new ToolMaterial() {
                    @Override
                    public int getDurability() {
                        return tool.material.durability;
                    }

                    @Override
                    public float getMiningSpeedMultiplier() {
                        return tool.material.miningSpeed;
                    }

                    @Override
                    public float getAttackDamage() {
                        return tool.material.attackDamage;
                    }

                    @Override
                    public int getMiningLevel() {
                        return tool.material.miningLevel;
                    }

                    @Override
                    public int getEnchantability() {
                        return tool.material.enchantability;
                    }

                    @Override
                    public Ingredient getRepairIngredient() {
                        return Ingredient.ofItems(Registry.ITEM.get(tool.material.repairItem));
                    }
                };
                switch (tool.toolType) {
                    case "pickaxe":
                        RegistryUtils.registerItem(new PickaxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                        .group(tool.information.getItemGroup())/*.rarity(tool.information.getRarity())*/.maxCount(tool.information.max_count)),
                                tool.information.name.id);
                        break;
                    case "shovel":
                        RegistryUtils.registerItem(new ShovelItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                        .group(tool.information.getItemGroup())/*.rarity(tool.information.getRarity())*/.maxCount(tool.information.max_count)),
                                tool.information.name.id);
                        break;
                    case "hoe":
                        RegistryUtils.registerItem(new HoeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                        .group(tool.information.getItemGroup())/*.rarity(tool.information.getRarity())*/.maxCount(tool.information.max_count)),
                                tool.information.name.id);
                        break;
                    case "axe":
                        RegistryUtils.registerItem(new AxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                        .group(tool.information.getItemGroup())/*.rarity(tool.information.getRarity())*/.maxCount(tool.information.max_count)),
                                tool.information.name.id);
                        break;
                }
                System.out.println(String.format("Registered a tool called %s", tool.information.name));
                ArtificeAssetGeneration.items.add(tool);
            }
            for(Block block : this.blocks) {
                FabricBlockSettings blockSettings = FabricBlockSettings.of(block.information.getMaterial()).sounds(block.information.getBlockSoundGroup())
                        .strength(block.information.destroy_time, block.information.explosion_resistance).drops(block.information.drop)
                        .collidable(block.information.collidable).slipperiness(block.information.slipperiness).emissiveLighting((state, world, pos) ->
                                block.information.is_emissive).nonOpaque();
                if (block.information.dynamicBounds) {
                    blockSettings.dynamicBounds();
                }
                if (block.information.randomTicks) {
                    blockSettings.ticksRandomly();
                }
                if (block.information.is_bouncy) {
                    blockSettings.jumpVelocityMultiplier(block.information.jump_velocity_modifier);
                }
                if (block.information.is_light_block) {
                    blockSettings.lightLevel(value -> block.information.luminance);
                }
                net.minecraft.block.Block blockImpl = null;
                if(block.additional_information != null) {
                    if(block.additional_information.rotatable) {
                        blockImpl = RegistryUtils.registerBlockWithoutItem(new HorizontalFacingBlockImpl(block, blockSettings), block.information.name.id);
                    }
                    if(block.additional_information.pillar) {
                        blockImpl = RegistryUtils.registerBlockWithoutItem(new PillarBlockImpl(block, blockSettings), block.information.name.id);
                    }
                    if(!block.additional_information.pillar && !block.additional_information.rotatable) {
                        blockImpl = RegistryUtils.registerBlockWithoutItem(new BlockImpl(block, blockSettings), block.information.name.id);
                    }
                } else {
                    blockImpl = RegistryUtils.registerBlockWithoutItem(new BlockImpl(block, blockSettings), block.information.name.id);
                }
                Item.Settings settings = new Item.Settings().group(block.information.getItemGroup());
                if (block.food_information != null) {
                    FoodComponent foodComponent = block.food_information.getBuilder().build();
                    settings.food(foodComponent);
                }
                if (block.information.fireproof) {
                    settings.fireproof();
                }
                RegistryUtils.registerItem(new CustomBlockItem(block, blockImpl, settings), block.information.name.id);
                net.minecraft.block.Block finalBlockImpl = blockImpl;

                if (block.ore_information != null) {
                    RuleTest test;
                    if (block.ore_information.test_type.equals("tag")) {
                        Tag<net.minecraft.block.Block> tag = BlockTags.getTagGroup().getTag(block.ore_information.target_state.block);
                        test = new TagMatchRuleTest(tag == null ? BlockTags.BASE_STONE_OVERWORLD : tag);
                    } else if (block.ore_information.test_type.equals("blockstate")) {
                        test = new BlockStateMatchRuleTest(getState(Registry.BLOCK.get(block.ore_information.target_state.block), block.ore_information.target_state.properties));
                    } else {
                        test = new BlockMatchRuleTest(Registry.BLOCK.get(block.ore_information.target_state.block));
                    }
                    ConfiguredFeature<?, ?> feature = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Utils.appendToPath(block.information.name.id, "_ore_feature"),
                            Feature.ORE.configure(
                                    new OreFeatureConfig(
                                            test,
                                            finalBlockImpl.getDefaultState(),
                                            block.ore_information.size
                                    )
                            ).decorate(
                                    Decorator.RANGE.configure(
                                            new RangeDecoratorConfig(
                                                    block.ore_information.config.bottom_offset,
                                                    block.ore_information.config.top_offset,
                                                    block.ore_information.config.maximum
                                            )
                                    )
                            ).method_30377(block.ore_information.config.maximum).spreadHorizontally().repeat(20));
                    BuiltinRegistries.BIOME.forEach(biome -> {
                        if (block.ore_information.biomes != null) {
                            for (String biome2 : block.ore_information.biomes) {
                                if (BuiltinRegistries.BIOME.getId(biome).toString().equals(biome2)) {
                                    BiomeUtils.addFeatureToBiome(biome, GenerationStep.Feature.UNDERGROUND_ORES, feature);
                                }
                            }
                        } else {
                            BiomeUtils.addFeatureToBiome(biome, GenerationStep.Feature.UNDERGROUND_ORES, feature);
                        }
                    });
                }
                System.out.println(String.format("Registered a block called %s", block.information.name));
                ArtificeAssetGeneration.blocks.add(block);
            }
            /*for(Entity entity : this.entities) {
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
            }*/
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
