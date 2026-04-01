package io.github.vampirestudios.obsidian.api.nexo;

import blue.endless.jankson.annotation.SerializedName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.ImmutableList;
import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.minecraft.oraxen.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Brightness;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Represents an Oraxen item with custom properties and mechanics.
 */
public class NexoItem {
	private static final Logger LOGGER = LogManager.getLogger(NexoItem.class);

	/**
	 * The unique identifier for the item.
	 */
	public Identifier id;
	/**
	 * The display name of the item.
	 */
	@JsonProperty("displayname")
	public String displayName;
	@JsonProperty("itemname")
	public String itemName;
	public String material;
	public String crucible_id;
	@JsonProperty("Pack")
	public Pack pack;
	@JsonProperty("Mechanics")
	public Mechanics mechanics;
	@JsonProperty("Components")
	public DataComponentMap components;
	public String permission;
	public String color;
	public List<String> lore = new ArrayList<>();
	public boolean excludeFromInventory = false;
	public boolean excludeFromCommands = false;
	public boolean using_components = false;
	public boolean unbreakable = false;

	public boolean can_have_banner = true;
	@JsonProperty("cooldown_ticks")
	public int cooldownTicks;
	@JsonProperty("repair_item")
	public Identifier repairItem = Identifier.withDefaultNamespace("air");
	@JsonProperty("block_sound")
	public Identifier blockSound = Identifier.withDefaultNamespace("item.shield.block");
	@JsonProperty("break_sound")
	public Identifier breakSound = Identifier.withDefaultNamespace("item.shield.break");

	public Component getName(Item item) {
		// If the item already has a custom ITEM_NAME component, use that
		if (item.components().has(DataComponents.ITEM_NAME)) {
			Component existing = item.components()
					.getOrDefault(DataComponents.ITEM_NAME, Component.literal("A"));
			return TagParser.QUICK_TEXT_WITH_STF.parseNode(existing.getString()).toComponent();
		}

		// Otherwise, fall back to displayName, then itemName, then "A"
		String name;
		if (isNameNotNull(displayName)) {
			name = displayName;
		} else if (isNameNotNull(itemName)) {
			name = itemName;
		} else {
			name = "A";
		}

		return TagParser.QUICK_TEXT_WITH_STF.parseNode(name).toComponent();
	}

	private boolean isNameNotNull(String name) {
		return name != null && !name.isBlank();
	}

	public Item getItem(Item.Properties properties) {
		ItemFactory factory = new ItemFactory();
		return factory.getItem(this, getItemType(), getTier(), properties);
	}

	public static class ItemFactory {
		private Item getItem(NexoItem nexoItem, ItemType type, ToolMaterial tier, Item.Properties properties) {
			return switch (type) {
				case AXE -> createAxe(nexoItem, tier, properties);
				case SHOVEL -> createShovel(nexoItem, tier, properties);
				case SWORD -> createSword(nexoItem, tier, properties);
				case HOE -> createHoe(nexoItem, tier, properties);
				case PICKAXE -> createPickaxe(nexoItem, tier, properties);
				case SHIELD -> createShield(nexoItem, properties);
				case BOW -> createBow(nexoItem, properties);
				case CROSSBOW -> createCrossbow(nexoItem, properties);
				case FISHING_ROD -> createFishingRod(nexoItem, properties);
				case TRIDENT -> createTrident(nexoItem, properties);
				case MACE -> createMace(nexoItem, properties);
				case ELYTRA	-> createElytra(nexoItem, properties);
				// All armor & animal‑armor types
				case HELMET,
					 CHESTPLATE,
					 LEGGINGS,
					 BOOTS,
					 HORSE_ARMOR,
					 LLAMA_CARPET,
					 WOLF_ARMOR	-> createArmor(nexoItem, properties);
				default -> createBasicItem(nexoItem, properties);
			};
		}

		// Method implementations to create specific tool types with given tier
		private Item createAxe(NexoItem nexoItem, ToolMaterial tier, Item.Properties properties) {
			return new AxeItemImpl(nexoItem, tier, getAttackDamage(tier, ItemType.AXE), getAttackSpeed(ItemType.AXE), properties);
		}

		private Item createShovel(NexoItem nexoItem, ToolMaterial tier, Item.Properties properties) {
			return new ShovelItemImpl(nexoItem, tier, getAttackDamage(tier, ItemType.SHOVEL), getAttackSpeed(ItemType.SHOVEL), properties);
		}

		private Item createSword(NexoItem nexoItem, ToolMaterial tier, Item.Properties properties) {
			return new ItemImpl(nexoItem, properties.sword(tier, getAttackDamage(tier, ItemType.SWORD), getAttackSpeed(ItemType.SWORD)));
		}

		private Item createHoe(NexoItem nexoItem, ToolMaterial tier, Item.Properties properties) {
			return new HoeItemImpl(nexoItem, tier, getAttackDamage(tier, ItemType.HOE), getAttackSpeed(ItemType.HOE), properties);
		}

		private Item createPickaxe(NexoItem nexoItem, ToolMaterial tier, Item.Properties properties) {
			return new ItemImpl(nexoItem, properties.pickaxe(tier, getAttackDamage(tier, ItemType.PICKAXE), getAttackSpeed(ItemType.PICKAXE)));
		}

		private Item createArmor(NexoItem nexoItem, Item.Properties properties) {
			if (nexoItem.mechanics != null && nexoItem.mechanics.armor != null) {
				io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial material;
				if (nexoItem.mechanics.armor.material != null && ContentRegistries.ARMOR_MATERIALS.containsKey(nexoItem.mechanics.armor.material)) {
					material = ContentRegistries.ARMOR_MATERIALS.getValue(nexoItem.mechanics.armor.material);
				} else if(nexoItem.mechanics.armor.armor_material != null) {
					material = nexoItem.mechanics.armor.armor_material;
				} else {
					material = null;
				}
				assert material != null;
				assert nexoItem.mechanics.armor.material != null;

				ResourceKey<EquipmentAsset> equipmentAsset = ResourceKey.create(Obsidian.ROOT_ID, nexoItem.mechanics.armor.material);
				ArmorMaterial customArmorMaterial = new ArmorMaterial(
						material.durability,
						material.defense,
						material.enchantability,
						SoundEvents.ARMOR_EQUIP_LEATHER,
						material.toughness,
						material.knockback_resistance,
						TagKey.create(Registries.ITEM, material.repair_tag),
						equipmentAsset
				);

				properties = properties.humanoidArmor(customArmorMaterial, ArmorType.valueOf(nexoItem.mechanics.armor.type.toUpperCase(Locale.ROOT)));
				Item item;
				if (nexoItem.mechanics.dyeable != null) {
					item = new DyeableArmorItemImpl(nexoItem, properties);
				} else {
					item = new CustomArmorItem(nexoItem, properties);
				}
				return item;
			}
			else return createBasicItem(nexoItem, properties);
		}

		/*private Item createShield(OraxenItem oraxenItem, Item.Properties properties) {
			return new ShieldItemImpl(oraxenItem, properties);
		}*/

		private Item createBow(NexoItem nexoItem, Item.Properties properties) {
			return new BowItemImpl(nexoItem, properties);
		}

		private Item createCrossbow(NexoItem nexoItem, Item.Properties properties) {
			return new CrossbowItemImpl(nexoItem, properties);
		}

		private Item createFishingRod(NexoItem nexoItem, Item.Properties properties) {
			return new FishingRodItemImpl(nexoItem, properties);
		}

		private Item createTrident(NexoItem nexoItem, Item.Properties properties) {
			nexoItem.mechanics.trident.thrown_item_model = nexoItem.id.withSuffix("_throwing");
			return new ItemImpl(nexoItem, properties);
		}

		private Item createMace(NexoItem nexoItem, Item.Properties properties) {
			return new MaceItemImpl(nexoItem, getAttackDamage(null, ItemType.MACE), getAttackSpeed(ItemType.MACE), properties);
		}

		private Item createBasicItem(NexoItem nexoItem, Item.Properties properties) {
			return new ItemImpl(nexoItem, properties);
		}

		private Item createShield(NexoItem nexoItem, Item.Properties properties) {
			return new ShieldItemImpl(nexoItem, properties);
		}

		private Item createElytra(NexoItem nexoItem, Item.Properties properties) {
			return new ElytraItemImpl(nexoItem, properties);
		}

		private float getAttackDamage(@Nullable ToolMaterial tier, ItemType type) {
			// Base damage above what fists do
			float finalDamage = switch (type) {
				case SWORD -> 3f; // swords do 3 more damage than fist
				case PICKAXE -> 1f; // pickaxes do 1 more damage than fist
				case AXE -> 6f; // axes do 6 more damage than fist
				case SHOVEL -> 1.5f; // shovels do 1.5 more damage than fist
				case HOE -> -3f; // hoes do 3 less damage than fist (historically, now they might do more)
				case MACE -> 5f; // hoes do 3 less damage than fist (historically, now they might do more)
				default -> 0f; // other items have no defined base damage increase
			};
			if (tier != null) finalDamage += tier.attackDamageBonus();
			return finalDamage; // Simple tier-based bonus, for a realistic approach you'd match exact Minecraft values.
		}

		private float getAttackSpeed(ItemType type) {
			return switch (type) {
				case SWORD -> -2.4f;
				case PICKAXE -> -2.8f;
				case AXE, SHOVEL -> -3.0f;
				case HOE -> 0.0f; // old hoes were slow but might now have different values
				case MACE -> -3.4f; // hoes do 3 less damage than fist (historically, now they might do more)
				default -> 0f; // other items might have neutral or no attack speed modifiers
			};
		}
	}

	public ToolMaterial getTier() {
		return switch(material.split("_")[0]) {
			case "STONE" -> ToolMaterial.STONE;
			case "IRON" -> ToolMaterial.IRON;
			case "GOLDEN" -> ToolMaterial.GOLD;
			case "DIAMOND" -> ToolMaterial.DIAMOND;
			case "NETHERITE" -> ToolMaterial.NETHERITE;
			default -> ToolMaterial.WOOD;
		};
	}

	public ItemType getItemType() {
		return Arrays.stream(ItemType.values())
				.filter(t -> material.contains(t.name()))
				.findFirst()
				.orElse(ItemType.BASIC);
	}

	public EquipmentSlot getEquipmentSlot() {
		String[] parts = material.split("_");
		if (parts.length < 2) {
			return EquipmentSlot.MAINHAND;
		}
		return switch (parts[1].toUpperCase()) {
			case "HELMET" -> EquipmentSlot.HEAD;
			case "CHESTPLATE" -> EquipmentSlot.CHEST;
			case "LEGGINGS" -> EquipmentSlot.LEGS;
			case "BOOTS" -> EquipmentSlot.FEET;
			default -> EquipmentSlot.MAINHAND;
		};
	}

	public enum ItemType {
		AXE,
		SHOVEL,
		SWORD,
		HOE,
		PICKAXE,
		SHIELD,
		BOW,
		CROSSBOW,
		FISHING_ROD,
		BASIC,
		HELMET,
		CHESTPLATE,
		LEGGINGS,
		BOOTS,
		MACE,
		ELYTRA,
		HORSE_ARMOR,
		LLAMA_CARPET,
		WOLF_ARMOR,
		TRIDENT
	}

	public static class Pack {
		public Identifier id;
		public boolean generate_model = false;
		public Identifier parent_model;
		public int custom_model_data;
		public Object textures;
		public Identifier texture;
		public Identifier model;
		public List<Identifier> pulling_models;
		public List<Identifier> damaged_models;
		public Identifier blocking_model;
		public Identifier charged_model;
		public Identifier firework_model;
		public Identifier cast_model;

		public Map<String, Identifier> getTextures() {
			Map<String, Identifier> texturesMap = new HashMap<>();
			if (textures instanceof List<?> list) {
				handleTextureList(texturesMap, list.stream().map(o -> o instanceof String s ? s : null).toList());
			} else if (textures instanceof Map<?, ?>) {
				handleTextureMap(texturesMap, (Map<String, Identifier>) textures);
			} else {
				if(generate_model) {
					LOGGER.info(id);
					throw new IllegalStateException("Textures field must be a list or a map");
				}
				else return null;
			}
			return texturesMap;
		}

		private void handleTextureList(Map<String, Identifier> texturesMap, List<String> textureList) {
			if (textureList == null || textureList.isEmpty()) {
				throw new IllegalArgumentException("Texture list cannot be null or empty");
			}

			String modelName = parent_model != null ? parent_model.getPath() : "";

			// Check if the model name suggests a specific item/block type
			if (modelName.equals("item/generated") || modelName.startsWith("item")) {
				// Handle item model textures
				for (int i = 0; i < textureList.size(); i++) {
					texturesMap.put("layer" + i, Identifier.fromNamespaceAndPath(id.getNamespace(), textureList.get(i)));
				}
			} else if (modelName.equals("block/cube_all")) {
				// Handle block model textures where all sides are the same
				if (!textureList.isEmpty()) {
					texturesMap.put("all", Identifier.fromNamespaceAndPath(id.getNamespace(), textureList.get(0)));
				}
			} else {
				// Default case for other models
				for (int i = 0; i < textureList.size(); i++) {
					texturesMap.put("texture" + i, Identifier.fromNamespaceAndPath(id.getNamespace(), textureList.get(i)));
				}
			}
		}

		private void handleTextureMap(Map<String, Identifier> texturesMap, Map<String, Identifier> textureMap) {
			if (textureMap == null || textureMap.isEmpty()) {
				throw new IllegalArgumentException("Texture map cannot be null or empty");
			}

			for (Map.Entry<String, Identifier> entry : textureMap.entrySet()) {
				String key = entry.getKey();
				Identifier value = entry.getValue();
				texturesMap.put(key, value);
			}
		}
	}

	public static class Mechanics {
		public Armor armor;
		public Furniture furniture;
		public NoteBlock noteblock;
		public StringBlock stringblock;
		public CustomBlock custom_block;
		public Durability durability;
		public Hat hat;
		public Equipable equipable;
		public Dyeable dyeable;
		public Cosmetic cosmetic;
		public Interactive interactive;
		public Attributes attributes;
		public Enchantable enchantable;
		public AdaptiveResistance adaptive_resistance;
		public EnergyHarvesting energy_harvesting;
		public SkillEmpowerment skill_empowerment;
		public TelekineticAbilities telekinetic_abilities;
		public BiomeAdaptation biome_adaptation;
		public PortalCreation portal_creation;
		public AuraOfInfluence aura_of_influence;
		public RealityBending reality_bending;
		public SummoningRituals summoning_rituals;
		public CognitiveEnhancement cognitive_enhancement;
		public WeatherControl weather_control;
		public SetBonus set_bonus;
		public Trident trident;
		public Bow bow;

		public static class BlockSounds {
			public Identifier place_sound;
			public Identifier break_sound;
			public Identifier hit_sound;
			public Identifier step_sound;
			public Identifier fall_sound;

			public SoundType getSoundType() {
				SoundEvent place = BuiltInRegistries.SOUND_EVENT.getValue(place_sound);
				SoundEvent breakSound1 = BuiltInRegistries.SOUND_EVENT.getValue(break_sound);
				SoundEvent hit = BuiltInRegistries.SOUND_EVENT.getValue(hit_sound);
				SoundEvent step = BuiltInRegistries.SOUND_EVENT.getValue(step_sound);
				SoundEvent fall = BuiltInRegistries.SOUND_EVENT.getValue(fall_sound);
				return new SoundType(1.0f, 1.0f, breakSound1, step, place, hit, fall);
			}
		}

		public static class Drop {
			public boolean silktouch;
			public List<Loot> loots;
			public String best_tool;

			public static class Loot {
				public String nexo_item;
				public float probability;
			}
		}

		public static class Lights {
			public boolean toggleable;
			public String toggled_model;
			public Identifier toggled_item_model;
			public List<Light> lights = new ArrayList<>();

			// Jackson will call this with the raw strings from YAML
			@JsonSetter("lights")
			public void setRawLights(List<String> raw) {
				for (String entry : raw) {
					// entry looks like "x..y,a..b,c..d LEVEL"
					String[] parts    = entry.split(" ");
					String  coords    = parts[0];
					int     lvl       = Integer.parseInt(parts[1]);
					String[] axes     = coords.split(",");
					List<Integer> xs  = expandIntRange(axes[0]);
					List<Integer> ys  = expandIntRange(axes[1]);
					List<Integer> zs  = expandIntRange(axes[2]);

					for (int x : xs) for (int y : ys) for (int z : zs) {
						lights.add(new Light(new Vec3(x, y, z), lvl));
					}
				}
			}

			private List<Integer> expandIntRange(String s) {
				if (s.contains("..")) {
					String[] r = s.split("\\.\\.");
					int start = Integer.parseInt(r[0]), end = Integer.parseInt(r[1]);
					List<Integer> out = new ArrayList<>();
					for (int i = start; i <= end; i++) out.add(i);
					return out;
				} else {
					return List.of(Integer.parseInt(s));
				}
			}

			public static class Light {
				public Vec3 pos;           // x,y,z
				public int lightLevel;     // 0–15
				public Light(Vec3 pos, int lightLevel) {
					this.pos        = pos;
					this.lightLevel = lightLevel;
				}
			}
		}

		public static class LimitedPlacing {
			public boolean roof = true;
			public boolean floor = true;
			public boolean wall = true;
			public LimitedPlacingType type = LimitedPlacingType.DENY;
			@JsonProperty("block_types")
			@SerializedName("block_types")
			public List<String> blockTypes;
			@JsonProperty("block_tags")
			@SerializedName("block_tags")
			public List<String> blockTags;
			@JsonProperty("nexo_blocks")
			@SerializedName("nexo_blocks")
			public List<String> nexoBlocks;

			public enum LimitedPlacingType {
				ALLOW, DENY
			}

			public static class RadiusLimitation {
				public int radius = -1;
				public int amount = -1;
			}
		}

		public static class Armor {
			public String type;
			public Identifier material;
			public Identifier texture;
			public io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial armor_material;
		}

		public static class Furniture {
			public String type;
			public BlockSounds block_sounds;
			public boolean barrier;
			public boolean restricted_player_rotation = false;
			public Drop drop;
			public LimitedPlacing limited_placing;
			public Connectable connectable;

			// new fields:
			public boolean rotatable = true;              // e.g. rotatable: false
			public RestrictedRotation restricted_rotation; // enum NONE, LOOSE, VERY_STRICT

			public Hitbox hitbox;                         // barriers list
			public List<String> seats;                      // seat offsets

			public Storage storage;                       // optional chest logic
			public Lights lights;                         // optional lamp logic

			public Properties properties;
			public List<ClickAction> clickActions;
			public String modelengine_id;

			public enum RestrictedRotation { NONE, LOOSE, VERY_STRICT }

			public static class Hitbox {
				public List<String> barriers;
				public List<String> interactions;
				public List<String> shulkers;

				// parsed data
				public List<Vec3>    barrierOffsets     = new ArrayList<>();
				public List<InteractionBox> interactionBoxes = new ArrayList<>();
				public List<ShulkerSpec>    shulkerSpecs     = new ArrayList<>();

				// helper classes
				public static record InteractionBox(Vec3 offset, double width, double height) {}
				public static record ShulkerSpec(BlockPos offset, double scale, double length, Direction dir) {}
			}

			public static class Storage {
				public String type;          // e.g. STORAGE
				public int rows;
				public String title;
				public Identifier open_sound;
				public Identifier close_sound;
			}

			public static class Connectable {
				/*public enum ConnectableItemType {
					ITEM_MODEL, ITEM;

					public ConnectableItemType validate(String itemId) {
						if (this == ITEM_MODEL) {
							return ITEM_MODEL;
						}
						return ITEM;
					}

					public static final ConnectableItemType DEFAULT_TYPE = ITEM_MODEL;
				}

				public class ConnectableMechanic {
					private final ConnectableItemType type = ConnectableItemType.DEFAULT_TYPE;
					private final Identifier def, straight, left, right, inner, outer;

					private final Supplier<ItemBuilder> defaultItem = () -> {
						if (type == ConnectableItemType.ITEM_MODEL) {
							return new ItemBuilder(Items.LEATHER_HORSE_ARMOR).setItemModel(def);
						} else {
							return NexoItems.itemFromId(def.value())
									.orElse(new ItemBuilder(Material.BARRIER));
						}
					};
					private final Supplier<ItemBuilder> straightItem = () -> buildDisplayItem(straight, "straight");
					private final Supplier<ItemBuilder> leftEndItem = () -> buildDisplayItem(left, "left");
					private final Supplier<ItemBuilder> rightEndItem = () -> buildDisplayItem(right, "right");
					private final Supplier<ItemBuilder> innerCornerItem = () -> buildDisplayItem(inner, "inner");
					private final Supplier<ItemBuilder> outerCornerItem = () -> buildDisplayItem(outer, "outer");

					public ConnectableMechanic(ConnectableItemType type,
											   Key def, Key straight, Key left,
											   Key right, Key inner, Key outer) {
						this.type     = type;
						this.def      = def;
						this.straight = straight;
						this.left     = left;
						this.right    = right;
						this.inner    = inner;
						this.outer    = outer;
					}

					public ConnectableMechanic(ConfigurationSection section) {
						this(
								section.getEnum("type", ConnectableItemType.class)
										.validate(section.getRoot().getName()),
								section.getKey("default")
										.orElseGet(() -> section.getRoot().getConfigurationSection("Components.item_model").getKey()
												.orElseGet(() -> section.getRoot().getConfigurationSection("Pack.model").getKey())),
								section.getKey("straight")
										.orElseGet(() -> section.getKey("default").appendSuffix("_straight")),
								section.getKey("left")
										.orElseGet(() -> section.getKey("default").appendSuffix("_left")),
								section.getKey("right")
										.orElseGet(() -> section.getKey("default").appendSuffix("_right")),
								section.getKey("inner")
										.orElseGet(() -> section.getKey("default").appendSuffix("_inner")),
								section.getKey("outer")
										.orElseGet(() -> section.getKey("default").appendSuffix("_outer"))
						);
					}

					private ItemBuilder buildDisplayItem(Identifier key, String suffix) {
						if (type == ConnectableItemType.ITEM_MODEL) {
							if (key != null) {
								return new ItemBuilder(Material.LEATHER_HORSE_ARMOR).setItemModel(key);
							} else {
								return defaultItem.get().clone()
										.setBlockStates(Map.of(BLOCKSTATE_KEY, suffix));
							}
						} else {
							return NexoItems.itemFromId(key.getPath())
									.orElse(defaultItem.get());
						}
					}

					public void scheduleUpdateState(Display.ItemDisplay ent, long delay) {
						SchedulerUtils.foliaScheduler.runAtEntityLater(ent,
								() -> updateState(ent), delay);
					}

					public void updateState(ItemDisplay ent) {
						ConnectType t = determineConnectableShape(ent);
						ent.getPersistentDataContainer().set(CONNECTABLE_KEY, ConnectType.DATA_TYPE, t);
						IFurniturePacketManager.furnitureBaseMap.get(ent.getUniqueId())
								.refreshItem(ent);
					}

					public void updateSurrounding(ItemDisplay ent) {
						SchedulerUtils.runTaskLater(2L, () -> {
							var loc = ent.getBlockLocation();
							var offsets = new BlockFace[]{BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH};
							for (BlockFace f : offsets) {
								IFurniturePacketManager.baseEntityFromHitbox(loc.clone().add(f.getDirection()))
										.filter(Entity::isValid)
										.ifPresent(e -> scheduleUpdateState((ItemDisplay)e, 1L));
							}
						});
					}

					private ConnectType determineConnectableShape(ItemDisplay ent) {
						ConnectType current = ent.getPersistentDataContainer()
								.get(CONNECTABLE_KEY, ConnectType.DATA_TYPE);
						var mech = NexoFurniture.furnitureMechanic(ent).orElse(null);
						if (mech == null) return ConnectType.DEFAULT;

						// compute faces & neighbors (omitted here for brevity)
						// ... (translate your Kotlin logic one-to-one) ...

						return current != null ? current : ConnectType.DEFAULT;
					}

					public ItemBuilder displayedItem(ItemDisplay ent) {
						ConnectType t = ent.getPersistentDataContainer()
								.getOrDefault(CONNECTABLE_KEY, ConnectType.DATA_TYPE, ConnectType.DEFAULT);
						return switch (t) {
							case STRAIGHT -> straightItem.get();
							case LEFT_END -> leftEndItem.get();
							case RIGHT_END -> rightEndItem.get();
							case INNER_CORNER, INNER_CORNER_ROTATED -> innerCornerItem.get();
							case OUTER_CORNER, OUTER_CORNER_ROTATED -> outerCornerItem.get();
							default -> defaultItem.get();
						};
					}

					public static final NamespacedKey CONNECTABLE_KEY =
							new NamespacedKey(NexoPlugin.getInstance(), BLOCKSTATE_KEY);
					private static final String BLOCKSTATE_KEY = "connectable";

					public enum ConnectType {
						DEFAULT, STRAIGHT, LEFT_END, RIGHT_END,
						OUTER_CORNER, OUTER_CORNER_ROTATED,
						INNER_CORNER, INNER_CORNER_ROTATED;

						public static final DataType<ConnectType> DATA_TYPE =
								DataType.asEnum(ConnectType.class);

						public boolean isCorner() {
							return this == OUTER_CORNER || this == INNER_CORNER
									|| this == OUTER_CORNER_ROTATED || this == INNER_CORNER_ROTATED;
						}

						public boolean isCornerRotated() {
							return this == OUTER_CORNER_ROTATED || this == INNER_CORNER_ROTATED;
						}

						public static ConnectType fromEntity(Entity e) {
							return e.getPersistentDataContainer().get(CONNECTABLE_KEY, DATA_TYPE);
						}
					}
				}*/
			}

			public static class Properties {
				public Color glowColor;
				public float viewRange;
				public Brightness brightness;
				public ItemDisplayContext display_transform = ItemDisplayContext.NONE;
				public String scale;
				public Display.BillboardConstraints trackingRotation = Display.BillboardConstraints.FIXED;
				public float shadowStrength;
				public float shadowRadius;
				public float displayWidth;
				public float displayHeight;
				public Vec3 translation;
//				public Quaternionf leftRotation = new Quaternionf();
//				public Quaternionf rightRotation = new Quaternionf();
			}

			public static class ClickAction {
				public List<String> conditions;
				public List<String> actions;
			}
		}

		public static class NoteBlock { }

		public static class StringBlock {
		}

		public static class CustomBlock {
			public BlockSounds block_sounds;
			public int custom_variation;
			public float hardness;
			public String model;
			public Drop drop;
			public LimitedPlacing limited_placing;
			public Lights light;
			public boolean immovable;
			public boolean blast_resistant;
			public CustomBlockType type;
			public LogStrip log_strip;
			public Directional directional;
			public Sapling sapling;

			public enum CustomBlockType {
				NOTEBLOCK, STRINGBLOCK, CHORUSBLOCK
			}

			public static class LogStrip {
				public String stripped_log;
				public String bark;
			}

			public static class Directional {
				public DirectionalType directional_type;
				public String parent_block;
				public String x_block;
				public String y_block;
				public String z_block;
				public String north_block;
				public String south_block;
				public String east_block;
				public String west_block;
				public String up_block;
				public String down_block;

				public enum DirectionalType {
					LOG, FURNACE, DROPPER
				}
			}

			public static class Sapling {
				public boolean grows_naturally;
				public int natural_growth_time;
				public boolean grows_from_bonemeal;
				public int bonemeal_growth_speedup;
				public Identifier grow_sound;
				public int min_light_level;
				public boolean requires_water_source;
				public Schemetic schematic;
				public boolean replace_blocks;
				public boolean copy_biomes;
				public boolean copy_entities;

				public static class Schemetic {
					public String schem;
					public float chance;
				}
			}
		}

		public record Durability(int value, boolean repairable) {}

		public static class Hat {
			public boolean enabled;
		}

		public static class Equipable {
			public String slot;
			public Identifier sound;
		}

		public static class Dyeable {
			public boolean enabled;
			public int default_color;
			public List<Pattern> patterns;  // Support for patterns

			public int getDefaultColor() {
				int color;
				if (this.default_color != 0)
					color = this.default_color;
				else
					color = 10511680;

				return color;
			}

			public static class Pattern {
				public String name;
				public String color;
			}
		}

		public static class Cosmetic {
			public List<String> valid_slots;
			public List<String> invalid_slots;
			public List<Animation> animations;  // Support for animations

			public static class Animation {
				public String type;
				public Identifier file;
			}
		}

		public static class Interactive {
			public List<Action> actions;  // Actions triggered by using the item

			public static class Action {
				public String trigger;  // E.g., "onUse", "onHit", "onEquip"
				public String effect;  // E.g., "heal", "damage"
				public Map<String, Object> parameters;  // Parameters for the effect

				public void performAction(Entity entity, Level world) {
					switch (effect) {
						case "heal":
							if (entity instanceof Player) {
								((Player) entity).heal((float) parameters.getOrDefault("amount", 0f));
							}
							break;
						case "damage":
							entity.hurt(entity.damageSources().generic(), (float) parameters.getOrDefault("amount", 0f));
							break;
						case "give_effect": {
							if (!(entity instanceof LivingEntity livingEntity)) break;
							livingEntity.addEffect(new MobEffectInstance(
									Holder.direct(BuiltInRegistries.MOB_EFFECT.getValue((Identifier) parameters.get("effect"))),
									(int) parameters.getOrDefault("duration", 20) / 20,
									(int) parameters.getOrDefault("amplifier", 0),
									(boolean) parameters.getOrDefault("ambient", false),
									(boolean) parameters.getOrDefault("visible", true)
							));
						}
						// Additional cases as needed
					}
				}
			}

			// Method to handle item usage based on trigger type
			public void handleUse(ItemStack itemStack, Level world, Entity entity, InteractionHand hand) {
				actions.stream()
						.filter(action -> "onUse".equals(action.trigger))
						.forEach(action -> action.performAction(entity, world));
			}
		}

		public static class Attributes {
			public List<AttributeModifierData> modifiers;

			public static class AttributeModifierData {
				public Identifier attributeName; // The name of the attribute, e.g., "generic.max_health"
				public double amount;
				public String operation; // "ADDITION", "MULTIPLY_BASE", "MULTIPLY_TOTAL"
				public String slot; // "mainhand", "offhand", "head", "chest", "legs", "feet", or "any"

				public AttributeModifier.Operation getOperation() {
					return switch (operation.toUpperCase()) {
						case "MULTIPLY_BASE" -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
						case "MULTIPLY_TOTAL" -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
						default -> AttributeModifier.Operation.ADD_VALUE;
					};
				}

				public EquipmentSlotGroup getEquipmentSlotGroup() {
					return switch (slot.toLowerCase()) {
						case "mainhand" -> EquipmentSlotGroup.MAINHAND;
						case "offhand" -> EquipmentSlotGroup.OFFHAND;
						case "head" -> EquipmentSlotGroup.HEAD;
						case "chest" -> EquipmentSlotGroup.CHEST;
						case "legs" -> EquipmentSlotGroup.LEGS;
						case "feet" -> EquipmentSlotGroup.FEET;
						case "any", "all" -> EquipmentSlotGroup.ANY;
						default -> EquipmentSlotGroup.ANY;
					};
				}
			}

			public ItemAttributeModifiers createAttributeModifiers(List<AttributeModifierData> modifiersData) {
				ImmutableList.Builder<ItemAttributeModifiers.Entry> entries = ImmutableList.builder();

				for (AttributeModifierData modifierData : modifiersData) {
					Attribute attribute = BuiltInRegistries.ATTRIBUTE.getValue(modifierData.attributeName);

					if (attribute != null) {
						Holder<Attribute> attributeHolder = Holder.direct(attribute);
						AttributeModifier.Operation operation = modifierData.getOperation();
						Identifier name = modifierData.attributeName;
						double amount = modifierData.amount;

						AttributeModifier attributeModifier = new AttributeModifier(name, amount, operation);
						EquipmentSlotGroup slotGroup = modifierData.getEquipmentSlotGroup();

						ItemAttributeModifiers.Entry entry = new ItemAttributeModifiers.Entry(attributeHolder, attributeModifier, slotGroup);
						entries.add(entry);
					} else {
						// Log warning or handle missing attribute
						LOGGER.warn("Attribute '{}' not found in registry.", modifierData.attributeName);
					}
				}

				return new ItemAttributeModifiers(entries.build());
			}

		}

		public static class Enchantable {
			public boolean allowed;
			public List<String> enchantments;  // List of allowed enchantment IDs
		}

		// Add new nested classes for each new mechanic here
		public static class AdaptiveResistance {
			public Map<String, Integer> damageTypeCounters = new HashMap<>();
			public int maxIncrease;
			public int effectDuration;

			public void onEntityDamage(Entity entity, DamageSource source) {
				String damageType = source.getMsgId();
				damageTypeCounters.put(damageType, damageTypeCounters.getOrDefault(damageType, 0) + 1);

				if (damageTypeCounters.get(damageType) > 3) {  // Arbitrary threshold for resistance boost
					if (entity instanceof Player) {
						((Player) entity).addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, effectDuration, calculateResistanceLevel()));
						LOGGER.info("Adaptive resistance applied to " + entity.getName().getString() + " for damage type " + damageType);
					}
					damageTypeCounters.put(damageType, 0);  // Reset counter after applying effect
				}
			}

			private int calculateResistanceLevel() {
				return Math.min(damageTypeCounters.values().stream().max(Integer::compare).orElse(0) / 5, maxIncrease);
			}
		}

		public static class EnergyHarvesting {
			public List<String> energySources;
			public double rechargeRate;

			public void harvestEnergy(Level world, BlockPos pos) {
				String currentEnergySource = determineEnergySource(world, pos);
				if (energySources.contains(currentEnergySource)) {
					LOGGER.info("Harvesting energy from source: " + currentEnergySource);
					// Implement recharge logic here
				}
			}

			private String determineEnergySource(Level world, BlockPos pos) {
				// Dummy implementation to determine the energy source based on location
				if (world.isBrightOutside()) {
					return "sunlight";
				} else if (world.isDarkOutside()) {
					return "moonlight";
				} else {
					return "redstone";
				}
			}
		}

		public static class SkillEmpowerment {
			public List<String> conditions;  // e.g., ["at_night", "below_y_30"]
			public Map<String, Integer> skillsBoost;  // e.g., {"mining_speed": 15, "attack_damage": 15}
			public int duration;

			public void applyIfConditionMet(Player player, String condition) {
				if (checkCondition(player, condition)) {
					skillsBoost.forEach((skill, boost) -> applySkillBoost(player, skill, boost));
					LOGGER.info("Skill empowerment applied to " + player.getName().getString());
				}
			}

			private boolean checkCondition(Player player, String condition) {
				// Implement specific condition checks
				return true; // Placeholder
			}

			private void applySkillBoost(Player player, String skill, int boost) {
				// Apply skill enhancement logic
				// This is a placeholder implementation
			}
		}

		public static class TelekineticAbilities {
			public int range;
			public List<String> allowedObjects;
			public int cooldown;

			public void activateAbility(Player player, Level world, Vec3 direction) {
				LOGGER.info("Activating telekinetic ability in the direction: " + direction);
				// Logic to manipulate objects/entities within range
			}
		}

		public static class BiomeAdaptation {
			public Map<String, List<String>> biomeEffects;  // e.g., {"desert": ["fire_resistance"], "snow": ["slowness_immunity"]}

			public void adaptToBiome(Entity entity, String biome) {
				List<String> effects = biomeEffects.getOrDefault(biome, new ArrayList<>());
				LOGGER.info("Adapting to biome with effects: " + effects);
				// Apply biome-specific effects to the entity
			}
		}

		public static class PortalCreation {
			public String trigger;
			public String portalType;
			public String destination;
			public int duration;

			public void createPortal(Level world, BlockPos pos) {
				LOGGER.info("Creating portal at location: " + pos);
				// Portal creation logic here
				// This could involve placing portal blocks or teleporting entities
			}
		}

		public static class AuraOfInfluence {
			public int radius;
			public Map<String, String> effects;  // e.g., {"enemies": "weakness", "allies": "strength"}
			public int effectDuration;

			public void createAura(Entity entity) {
				LOGGER.info("Creating aura of influence around: " + entity.getName());
				// Logic to create an aura affecting others within the radius
			}
		}

		public static class RealityBending {
			public List<Map<String, Object>> effects;  // e.g., [{"effect": "gravity_reverse", "duration": 5}, {"effect": "time_slow", "duration": 10}]

			public void bendReality(Level world, BlockPos pos) {
				LOGGER.info("Bending reality at: " + pos);
				// Implement reality bending effects at the specified location
			}
		}

		public static class SummoningRituals {
			public List<String> requiredSetup;
			public EntityType<?> summonType;
			public int cooldown;

			public void performRitual(Level world, BlockPos pos) {
				if (checkSetup(world, pos)) {
					Entity entity = summonType.create(world, EntitySpawnReason.SPAWN_ITEM_USE);
					assert entity != null;
					entity.setPos(pos.getX(), pos.getY(), pos.getZ());
					world.addFreshEntity(entity);

					LOGGER.info("Summoning ritual performed at " + pos);
				}
			}

			private boolean checkSetup(Level world, BlockPos pos) {
				// Check if the setup around `pos` matches `requiredSetup`
				return true; // Placeholder
			}
		}

		public static class CognitiveEnhancement {
			public String effect;  // e.g., "increase_game_tick_speed"
			public Map<String, Integer> effects;
			public float newTickSpeed = 1.0f;
			public int duration;
			public double gravity;

			private List<Effect> activeEffects = new ArrayList<>();

			public void enhanceCognition(LivingEntity entity) {
				if (!(entity instanceof Player)) {
					return; // Only apply to players
				}
				float defaultTickSpeed = entity.level().tickRateManager().tickrate();

				if (effect.equals("random")) {
					applyRandomEffect(entity, defaultTickSpeed);
				} else {
					applySpecificEffect(entity, effect, defaultTickSpeed);
				}
			}

			private void applyRandomEffect(LivingEntity entity, float defaultTickSpeed) {
				List<Effect> effectsToApply = createEffects(entity, defaultTickSpeed);
				Random random = new Random();
				Effect randomEffect = effectsToApply.get(random.nextInt(effectsToApply.size()));
				randomEffect.apply(entity);
				activeEffects.add(randomEffect);
			}

			private void applySpecificEffect(LivingEntity entity, String effectName, float defaultTickSpeed) {
				List<Effect> effectsToApply = createEffects(entity, defaultTickSpeed);
				for (Effect effect : effectsToApply) {
					if (effect.getName().equals(effectName)) {
						effect.apply(entity);
						activeEffects.add(effect);
						break;
					}
				}
			}

			private List<Effect> createEffects(LivingEntity entity, float defaultTickSpeed) {
				List<Effect> effects = new ArrayList<>();
				effects.add(new Effect("increase_game_tick_speed",
						e -> increaseGameTickSpeed(entity, newTickSpeed),
						e -> increaseGameTickSpeed(entity, defaultTickSpeed),
						duration));

				effects.add(new Effect("freeze_game_tick_speed",
						e -> freezeGameTickSpeed(entity, true),
						e -> freezeGameTickSpeed(entity, false),
						duration));

				effects.add(new Effect("temporal_distortion",
						e -> applyTemporalDistortion(entity),
						e -> applyTemporalDistortion(entity),
						duration));

				effects.add(new Effect("enhanced_reflexes",
						e -> applyEnhancedReflexes(entity),
						e -> applyEnhancedReflexes(entity),
						duration));

				effects.add(new Effect("cognitive_overload",
						e -> applyCognitiveOverload(entity),
						e -> applyCognitiveOverload(entity),
						duration));

				effects.add(new Effect("invisibility_flash",
						e -> applyInvisibilityFlash(entity, duration),
						e -> {},
						duration));

				effects.add(new Effect("change_gravity",
						e -> changePlayerGravity(entity, duration, gravity),
						e -> changePlayerGravity(entity, duration, 0.08),  // Revert to default gravity
						duration));

				effects.add(new Effect("healing_rain",
						e -> applyHealingRain(entity, duration),
						e -> {},
						duration));

				effects.add(new Effect("mystery_teleport",
						e -> applyMysteryTeleport(entity),
						e -> {},
						0));

				effects.add(new Effect("apply_clone",
						e -> applyClone(entity, duration),
						e -> {},
						duration));

				return effects;
			}

			// This method should be called every tick, replace with appropriate tick event handler in your modding framework
			public void onTick(LivingEntity entity) {
				List<Effect> toRemove = new ArrayList<>();
				for (Effect effect : activeEffects) {
					effect.tick(entity);
					if (effect.isExpired()) {
						toRemove.add(effect);
					}
				}
				activeEffects.removeAll(toRemove);
			}

			private @NotNull Runnable getRunnable(LivingEntity entity) {
				RandomEffectSelector effectSelector = new RandomEffectSelector(List.of(
						() -> increaseGameTickSpeed(entity, newTickSpeed),
						() -> freezeGameTickSpeed(entity, true),
						() -> applyTemporalDistortion(entity),
						() -> applyEnhancedReflexes(entity),
						() -> applyCognitiveOverload(entity),
						() -> applyInvisibilityFlash(entity, 10),
						() -> changePlayerGravity(entity, 30, 0.04),  // Reduced gravity
						() -> changePlayerGravity(entity, 30, 0.12),  // Increased gravity
						() -> applyHealingRain(entity, 5),
						() -> applyMysteryTeleport(entity),
						() -> applyClone(entity, 30)
				));
				return () -> {
					LOGGER.info("Cognition enhancement effect applied.");
					switch (effect) {
						case "random":
							effectSelector.applyRandomEffect();
							break;
						case "increase_game_tick_speed":
							increaseGameTickSpeed(entity, newTickSpeed);
							break;
						case "freeze_game_tick_speed":
							freezeGameTickSpeed(entity, true);
							break;
						case "temporal_distortion":
							applyTemporalDistortion(entity);
							break;
						case "enhanced_reflexes":
							applyEnhancedReflexes(entity);
							break;
						case "cognitive_overload":
							applyCognitiveOverload(entity);
							break;
						case "invisibility_flash":
							applyInvisibilityFlash(entity, duration);
							break;
						case "change_gravity":
							changePlayerGravity(entity, duration, gravity);
							break;
						case "healing_rain":
							applyHealingRain(entity, duration);
							break;
						case "mystery_teleport":
							applyMysteryTeleport(entity);
							break;
						case "apply_clone":
							applyClone(entity, duration);
							break;
					}
				};
			}

			private void increaseGameTickSpeed(Entity entity, float newTickSpeed) {
				entity.level().tickRateManager().setTickRate(newTickSpeed);
				LOGGER.info("Game ticks are now set to " + newTickSpeed);
			}

			private void freezeGameTickSpeed(Entity entity, boolean freeze) {
				entity.level().tickRateManager().setFrozen(freeze);
				LOGGER.info("Game ticks are now frozen");
			}

			private void applyTemporalDistortion(LivingEntity entity) {
				if (newTickSpeed > 1.0f)
					entity.addEffect(new MobEffectInstance(MobEffects.HASTE, duration * 20, 1));
				else
					entity.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, duration * 20, 1));
			}

			private void applyEnhancedReflexes(LivingEntity entity) {
				entity.addEffect(new MobEffectInstance(MobEffects.SPEED, duration * 20, 1));
				entity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, duration * 20, 1));
			}

			private void applyCognitiveOverload(LivingEntity entity) {
				entity.addEffect(new MobEffectInstance(MobEffects.STRENGTH, duration * 10, 2)); // Boost for half the duration
//				scheduler.schedule(() -> entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration * 20, 1)), duration * 10, TimeUnit.SECONDS); // Weakness after boost
			}

			public static void applyInvisibilityFlash(LivingEntity player, int duration) {
				player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration * 20, 0));
			}

			private static final UUID GRAVITY_MODIFIER_ID = UUID.fromString("c8b26db0-eeca-4d94-9b60-5ba9d7be0c6b");

			/**
			 * Changes the gravity for a living entity.
			 *
			 * @param entity The entity to modify.
			 * @param duration Duration in seconds for how long the gravity change should last.
			 * @param gravityFactor A double value that specifies the new gravity value (range from -1.0 to 1.0).
			 */
			public static void changePlayerGravity(LivingEntity entity, int duration, double gravityFactor) {
				// Ensure gravityFactor is within the valid range
				double validatedGravityFactor = Math.max(-1.0, Math.min(1.0, gravityFactor));

				// Calculate the change needed from the default gravity
				double defaultGravity = 0.08;  // Default gravity value for Minecraft
				double modifierValue = validatedGravityFactor - defaultGravity;

				AttributeModifier gravityModifier = new AttributeModifier(Identifier.fromNamespaceAndPath("obsidian", "custom_gravity"), modifierValue, AttributeModifier.Operation.ADD_VALUE);

				applyModifierWithTimeout(entity, gravityModifier, duration);
			}

			private static void applyModifierWithTimeout(LivingEntity entity, AttributeModifier modifier, int duration) {
				AttributeInstance attributeInstance = entity.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.GRAVITY);
				if (attributeInstance == null) {
					return; // This entity does not support the specified attribute
				}

				// Remove old modifier if it exists to prevent stacking
				if (attributeInstance.getModifier(modifier.id()) != null) {
					attributeInstance.removeModifier(modifier);
				}

				// Apply the new modifier
				attributeInstance.addPermanentModifier(modifier);

				LOGGER.info("Custom gravity (" + modifier.amount() + ") applied to " + entity.getName().getString());

				if (duration != -1) {
					ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
					scheduler.schedule(() -> {
						attributeInstance.removeModifier(modifier.id());
						LOGGER.info("Gravity effect reverted for " + entity.getName().getString());
					}, duration, TimeUnit.SECONDS);
					scheduler.schedule(scheduler::shutdown, duration + 5, TimeUnit.SECONDS);
				}
			}

			public static void applyHealingRain(LivingEntity player, int duration) {
				// Assuming this affects all players in a radius around 'player'
				ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
				scheduler.execute(() -> player.level().getEntities(player, player.getBoundingBox().inflate(5)).forEach(e ->
						((LivingEntity) e).heal(5.0F)));
				scheduler.schedule(scheduler::shutdown, duration, TimeUnit.SECONDS);
				LOGGER.info("Healing rain activated around " + player.getName().getString());
			}

			public static void applyMysteryTeleport(LivingEntity player) {
				Level world = player.level();
				BlockPos randomPos = player.blockPosition().offset(world.getRandom().nextInt(100) - 50, 0, world.getRandom().nextInt(100) - 50);
				player.teleportTo(randomPos.getX(), world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, randomPos.getX(), randomPos.getZ()), randomPos.getZ());
			}

			public static void applyClone(LivingEntity player, int duration) {
				// Clone logic; actual implementation would depend on the modding API
				LOGGER.info("Creating a clone for " + player.getName().getString());
			}
		}

		public static class WeatherControl {
			public String weatherType; // "rain", "clear", "thunder"
			public int duration; // in seconds

			public void changeWeather(Level world) {
				if (world instanceof ServerLevel serverWorld) {
					switch (weatherType.toLowerCase()) {
						case "clear" -> serverWorld.getWeatherData().setClearWeatherTime(duration * 20);
						case "rain" -> {
							serverWorld.getWeatherData().setRaining(true);
							serverWorld.getWeatherData().setRainTime(duration * 20);
						}
						case "thunder" -> {
							serverWorld.getWeatherData().setThundering(true);
							serverWorld.getWeatherData().setThunderTime(duration * 20);
						}
						default -> throw new IllegalStateException("Unexpected value: " + weatherType.toLowerCase());
					}
				}
			}
		}

		public static class ElementalAffinity {
			public String elementType; // "fire", "water", "earth", "air"
			public Map<String, Double> effectiveness; // e.g., {"ice_mob": 1.5, "fire_mob": 0.5}

			public double getDamageMultiplier(Entity target) {
				String targetType = target.getType().toShortString();
				return effectiveness.getOrDefault(targetType, 1.0);
			}
		}

		public static class TimeManipulation {
			public double speedFactor; // e.g., 0.5 for slow down, 2.0 for speed up
			public int radius; // Affected area radius in blocks

			public void applyTimeEffect(Level world, BlockPos center) {
				// Apply effects to entities within radius
				List<Entity> entities = world.getEntities(null, new AABB(center).inflate(radius));
				for (Entity entity : entities) {
					entity.setDeltaMovement(entity.getDeltaMovement().multiply(speedFactor, 1.0, speedFactor));
				}
				// Optionally adjust block updates like crop growth
			}
		}

		public static class ResourceRegeneration {
			public String resourceType; // "tree", "ore"
			public int regenerationRate; // in seconds

			public void regenerateResource(Level world, BlockPos pos) {
				if ("tree".equals(resourceType)) {
					// Code to plant a sapling at the specified position
				} else if ("ore".equals(resourceType)) {
					// Code to regenerate ore blocks in nearby stone
				}
			}
		}

		public static class CompanionSummoning {
			public EntityType<?> companionType;
			public int duration; // in seconds

			public void summonCompanion(Level world, Player player) {
				Entity companion = companionType.create(world, EntitySpawnReason.SPAWN_ITEM_USE);
				if (companion != null) {
					companion.setPos(player.getX(), player.getY(), player.getZ());
					world.addFreshEntity(companion);
					// Optional: Add AI goals to follow and protect the player
				}
			}
		}

		public static class CursedItem {
			public List<MobEffectInstance> benefits;
			public List<MobEffectInstance> drawbacks;

			public void applyCurseEffects(Player player) {
				for (MobEffectInstance effect : benefits) {
					player.addEffect(new MobEffectInstance(effect));
				}
				for (MobEffectInstance effect : drawbacks) {
					player.addEffect(new MobEffectInstance(effect));
				}
			}
		}

		public static class DimensionTravel {
			public ResourceKey<Level> targetDimension;

			public void travelToDimension(ServerPlayer player) {
				player.teleport(TeleportTransition.missingRespawnBlock(player, TeleportTransition.PLAY_PORTAL_SOUND));
			}
		}

		public static class SetBonus {
			public String setName;
			public List<MobEffectInstance> bonusEffects;
			public Map<EquipmentSlot, Identifier> requiredItems;

			private boolean bonusApplied = false;

			public void checkAndApplyBonus(Player player) {
				boolean hasFullSet = true;
				for (Map.Entry<EquipmentSlot, Identifier> entry : requiredItems.entrySet()) {
					EquipmentSlot slot = entry.getKey();
					Identifier requiredItemId = entry.getValue();
					ItemStack equippedItem = player.getItemBySlot(slot);

					if (!isMatchingItem(equippedItem, requiredItemId)) {
						hasFullSet = false;
						break;
					}
				}

				if (hasFullSet && !bonusApplied) {
					applyBonusEffects(player);
					bonusApplied = true;
				} else if (!hasFullSet && bonusApplied) {
					removeBonusEffects(player);
					bonusApplied = false;
				}
			}

			private boolean isMatchingItem(ItemStack itemStack, Identifier requiredItemId) {
				if (itemStack.isEmpty()) {
					return false;
				}
				Item item = itemStack.getItem();
				Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
				return requiredItemId.equals(itemId);
			}

			private void applyBonusEffects(Player player) {
				for (MobEffectInstance effect : bonusEffects) {
					// Apply the effect only if the player doesn't already have it
					if (!player.hasEffect(effect.getEffect())) {
						player.addEffect(new MobEffectInstance(effect));
					}
				}
			}

			private void removeBonusEffects(Player player) {
				for (MobEffectInstance effect : bonusEffects) {
					player.removeEffect(effect.getEffect());
				}
			}

			public void parseRequiredItems(Map<String, String> itemsMap) {
				requiredItems = new HashMap<>();
				for (Map.Entry<String, String> entry : itemsMap.entrySet()) {
					EquipmentSlot slot = getEquipmentSlotFromString(entry.getKey());
					Identifier itemId = Identifier.parse(entry.getValue());
					requiredItems.put(slot, itemId);
				}
			}

			private EquipmentSlot getEquipmentSlotFromString(String slotName) {
				return switch (slotName.toLowerCase()) {
					case "head" -> EquipmentSlot.HEAD;
					case "chest" -> EquipmentSlot.CHEST;
					case "legs" -> EquipmentSlot.LEGS;
					case "feet" -> EquipmentSlot.FEET;
					default -> throw new IllegalArgumentException("Invalid equipment slot: " + slotName);
				};
			}
		}

		public static class Alchemy {
			public List<PotionRecipe> customRecipes;

			public static class PotionRecipe {
				public ItemStack input;
				public ItemStack reagent;
				public Potion output;
			}

			public void registerCustomRecipes() {
				// Register recipes with the brewing stand or custom crafting system
			}
		}

		public static class EnvironmentalReactive {
			public Map<String, Double> biomeModifiers; // e.g., {"desert": 1.2, "forest": 0.8}

			public double getEnvironmentalModifier(Level world, BlockPos pos) {
				String biomeName = world.getBiome(pos).unwrapKey().get().identifier().getPath();
				return biomeModifiers.getOrDefault(biomeName, 1.0);
			}
		}

		public static class SkillRequirement {
			public String skillName;
			public int requiredLevel;

			public boolean canUse(Player player) {
				int playerSkillLevel = getPlayerSkillLevel(player, skillName);
				return playerSkillLevel >= requiredLevel;
			}

			private int getPlayerSkillLevel(Player player, String skill) {
				// Retrieve the player's skill level from a skills system
				return 0; // Placeholder
			}
		}

		public static class QuestItem {
			public String questID;
			public List<String> unlockAbilities;

			public void updateItemAbilities(Player player) {
				if (isQuestCompleted(player, questID)) {
					// Enable abilities
				}
			}

			private boolean isQuestCompleted(Player player, String questID) {
				// Check the player's quest status
				return false; // Placeholder
			}
		}

		public static class EnergyConsumer {
			public int energyCost;

			public boolean consumeEnergy(Player player) {
				int playerEnergy = getPlayerEnergy(player);
				if (playerEnergy >= energyCost) {
					setPlayerEnergy(player, playerEnergy - energyCost);
					return true;
				}
				return false;
			}

			private int getPlayerEnergy(Player player) {
				// Retrieve player's current energy
				return 0; // Placeholder
			}

			private void setPlayerEnergy(Player player, int newEnergy) {
				// Update player's energy
			}
		}

		public static class Transformative {
			public EntityType<?> targetEntityType;

			public void transformEntity(Entity entity) {
				Entity transformedEntity = targetEntityType.create(entity.level(), EntitySpawnReason.SPAWN_ITEM_USE);
				if (transformedEntity != null) {
					transformedEntity.moveOrInterpolateTo(entity.position(), entity.getYRot(), entity.getXRot());
					entity.level().addFreshEntity(transformedEntity);
					entity.remove(Entity.RemovalReason.DISCARDED);
				}
			}
		}

		public static class Trident {
			public Identifier thrown_item_model;
			public String thrown_item;
			public ItemDisplayContext transform = ItemDisplayContext.NONE;
			public Vec2 rotation = new Vec2(0, 0);
		}

		public static class Bow {
			public int range = 15;
		}
	}
}
