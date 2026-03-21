package io.github.vampirestudios.obsidian.registry;

import io.github.vampirestudios.obsidian.api.crucible.*;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import io.github.vampirestudios.obsidian.api.obsidian.*;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.block.BlockSetType;
import io.github.vampirestudios.obsidian.api.obsidian.block.CustomSoundGroup;
import io.github.vampirestudios.obsidian.api.obsidian.block.WoodType;
import io.github.vampirestudios.obsidian.api.obsidian.cauldronTypes.CauldronType;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.api.obsidian.emoji.Emoji;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid;
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.api.obsidian.particle.Particle;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.api.obsidian.ui.GUI;
import io.github.vampirestudios.obsidian.api.obsidian.ui.HUD;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerBiomeType;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerProfession;
import io.github.vampirestudios.obsidian.api.obsidian.world.Biome;
import io.github.vampirestudios.obsidian.api.obsidian.world.Structure;
import io.github.vampirestudios.obsidian.api.obsidian.world.Tree;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ToolMaterial;

import static io.github.vampirestudios.obsidian.Obsidian.id;

public class ContentRegistries {
	public static ResourceKey<Registry<Item>> ITEMS_KEY = ResourceKey.createRegistryKey(id("items"));
	public static ResourceKey<Registry<CrucibleSkill>> SKILLS_KEY = ResourceKey.createRegistryKey(id("crucible_skills"));

	public static Registry<Item> ITEMS = FabricRegistryBuilder.create(ITEMS_KEY).buildAndRegister();

	public static Registry<NexoItem> NEXO_ITEMS = FabricRegistryBuilder.create(NexoItem.class, id("nexo_items")).buildAndRegister();
	public static Registry<CrucibleItem> CRUCIBLE_ITEMS = FabricRegistryBuilder.create(CrucibleItem.class, id("crucible_items")).buildAndRegister();
	public static Registry<CrucibleSkill> CRUCIBLE_SKILLS = FabricRegistryBuilder.create(SKILLS_KEY).buildAndRegister();
	public static Registry<Effect> CRUCIBLE_EFFECTS = FabricRegistryBuilder.create(Effect.class, id("crucible_effects")).buildAndRegister();
	public static Registry<CrucibleItemSet> ITEM_SETS = FabricRegistryBuilder.create(CrucibleItemSet.class, id("item_sets")).buildAndRegister();
	public static Registry<CrucibleAugmentType> AUGMENT_TYPES = FabricRegistryBuilder.create(CrucibleAugmentType.class, id("augment_types")).buildAndRegister();
	public static Registry<CrucibleAugment> AUGMENTS = FabricRegistryBuilder.create(CrucibleAugment.class, id("augments")).buildAndRegister();
	public static Registry<FoodItem> FOODS = FabricRegistryBuilder.create(FoodItem.class, id("foods")).buildAndRegister();
	public static Registry<FoodComponent> FOOD_COMPONENTS = FabricRegistryBuilder.create(FoodComponent.class, id("custom_food_components")).buildAndRegister();
	public static Registry<CustomSoundGroup> BLOCK_SOUND_GROUPS = FabricRegistryBuilder.create(CustomSoundGroup.class, id("block_sound_groups")).buildAndRegister();
	public static Registry<BlockSettings> BLOCK_SETTINGS = FabricRegistryBuilder.create(BlockSettings.class, id("block_settings")).buildAndRegister();
	public static Registry<ToolMaterial> TOOL_MATERIALS = FabricRegistryBuilder.create(ToolMaterial.class, id("tool_materials")).buildAndRegister();
	public static Registry<Tier> TIERS = FabricRegistryBuilder.create(Tier.class, id("tiers")).buildAndRegister();
	public static Registry<ItemSettings> ITEM_SETTINGS = FabricRegistryBuilder.create(ItemSettings.class, id("item_settings")).buildAndRegister();
	public static Registry<Item> ITEM_TEMPLATES = FabricRegistryBuilder.create(Item.class, id("item_templates")).buildAndRegister();
	public static Registry<BlockSetType> BLOCK_SET_TYPES = FabricRegistryBuilder.create(BlockSetType.class, id("block_set_types")).buildAndRegister();
	public static Registry<WoodType> WOOD_TYPES = FabricRegistryBuilder.create(WoodType.class, id("wood_types")).buildAndRegister();
	public static Registry<KeyBinding> KEY_BINDINGS = FabricRegistryBuilder.create(KeyBinding.class, id("key_bindings")).buildAndRegister();
	public static Registry<Particle> PARTICLES = FabricRegistryBuilder.create(Particle.class, id("particles")).buildAndRegister();
	public static Registry<WeaponItem> WEAPONS = FabricRegistryBuilder.create(WeaponItem.class, id("weapons")).buildAndRegister();
	public static Registry<RangedWeaponItem> RANGED_WEAPONS = FabricRegistryBuilder.create(RangedWeaponItem.class, id("ranged_weapons")).buildAndRegister();
	public static Registry<ToolItem> TOOLS = FabricRegistryBuilder.create(ToolItem.class, id("tools")).buildAndRegister();
	public static Registry<SoundPlayingItem> SOUND_PLAYING_ITEMS = FabricRegistryBuilder.create(SoundPlayingItem.class, id("sound_playing_items")).buildAndRegister();
	public static Registry<Block> BLOCKS = FabricRegistryBuilder.create(Block.class, id("blocks")).buildAndRegister();
	public static Registry<FuelSource> FUEL_SOURCES = FabricRegistryBuilder.create(FuelSource.class, id("fuel_sources")).buildAndRegister();
	public static Registry<Block> ORES = FabricRegistryBuilder.create(Block.class, id("ores")).buildAndRegister();
	public static Registry<Potion> POTIONS = FabricRegistryBuilder.create(Potion.class, id("potions")).buildAndRegister();
	public static Registry<Command.CommandNode> COMMANDS = FabricRegistryBuilder.create(Command.CommandNode.class, id("commands")).buildAndRegister();
	public static Registry<StatusEffect> STATUS_EFFECTS = FabricRegistryBuilder.create(StatusEffect.class, id("status_effects")).buildAndRegister();
	public static Registry<ItemGroup> ITEM_GROUPS = FabricRegistryBuilder.create(ItemGroup.class, id("item_groups_registry")).buildAndRegister();
	public static Registry<CreativeTab> CREATIVE_TABS = FabricRegistryBuilder.create(CreativeTab.class, id("creative_tabs")).buildAndRegister();
//	public static Registry<TabbedGroup> EXPANDED_ITEM_GROUPS = FabricRegistryBuilder.create(TabbedGroup.class, id("expanded_item_groups_registry")).buildAndRegister();
	public static Registry<CondensedEntry> CONDENSED_ITEM_ENTRIES = FabricRegistryBuilder.create(CondensedEntry.class, id("condensed_item_entries")).buildAndRegister();
	public static Registry<Entity> ENTITIES = FabricRegistryBuilder.create(Entity.class, id("entities")).buildAndRegister();
	public static Registry<EntityModel> ENTITY_MODELS = FabricRegistryBuilder.create(EntityModel.class, id("entity_models")).buildAndRegister();
	public static Registry<ArmorMaterial> ARMOR_MATERIALS = FabricRegistryBuilder.create(ArmorMaterial.class, id("armor_materials")).buildAndRegister();
	public static Registry<ArmorModel> ARMOR_MODELS = FabricRegistryBuilder.create(ArmorModel.class, id("armor_models")).buildAndRegister();
	public static Registry<ArmorItem> ARMORS = FabricRegistryBuilder.create(ArmorItem.class, id("armors")).buildAndRegister();
	public static Registry<Elytra> ELYTRAS = FabricRegistryBuilder.create(Elytra.class, id("elytras")).buildAndRegister();
	public static Registry<Cosmetic> COSMETICS = FabricRegistryBuilder.create(Cosmetic.class, id("cosmetics")).buildAndRegister();
//	public static Registry<ZoomableItem> ZOOMABLE_ITEMS = FabricRegistryBuilder.create(ZoomableItem.class, id("zoomable_items")).buildAndRegister();
	public static Registry<CauldronType> CAULDRON_TYPES = FabricRegistryBuilder.create(CauldronType.class, id("cauldron_types")).buildAndRegister();
	public static Registry<ShieldItem> SHIELDS = FabricRegistryBuilder.create(ShieldItem.class, id("shields")).buildAndRegister();
	public static Registry<VillagerProfession> VILLAGER_PROFESSIONS = FabricRegistryBuilder.create(VillagerProfession.class, id("villager_professions")).buildAndRegister();
	public static Registry<VillagerBiomeType> VILLAGER_BIOME_TYPES = FabricRegistryBuilder.create(VillagerBiomeType.class, id("villager_biome_types")).buildAndRegister();
	public static Registry<Fluid> FLUIDS = FabricRegistryBuilder.create(Fluid.class, id("fluids")).buildAndRegister();
	public static Registry<Emoji> EMOJIS = FabricRegistryBuilder.create(Emoji.class, id("emojis")).buildAndRegister();
	public static Registry<GUI> GUIS = FabricRegistryBuilder.create(GUI.class, id("guis")).buildAndRegister();
	public static Registry<HUD> HUDS = FabricRegistryBuilder.create(HUD.class, id("huds")).buildAndRegister();

	//World Generation
	public static Registry<Tree> TREES = FabricRegistryBuilder.create(Tree.class, id("trees")).buildAndRegister();
	public static Registry<Structure> STRUCTURES = FabricRegistryBuilder.create(Structure.class, id("structures")).buildAndRegister();
	public static Registry<Biome> BIOMES = FabricRegistryBuilder.create(Biome.class, id("biomes")).buildAndRegister();
}
