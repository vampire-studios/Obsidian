package io.github.vampirestudios.obsidian.registry;

import io.github.vampirestudios.obsidian.api.obsidian.*;
import io.github.vampirestudios.obsidian.api.obsidian.block.*;
import io.github.vampirestudios.obsidian.api.obsidian.cauldronTypes.CauldronType;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.api.obsidian.emoji.Emoji;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
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
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;

import static io.github.vampirestudios.obsidian.Obsidian.id;

public class ContentRegistries {
	public static Registry<Item> ITEMS = FabricRegistryBuilder.createSimple(Item.class, id("items")).buildAndRegister();
	public static Registry<FoodItem> FOODS = FabricRegistryBuilder.createSimple(FoodItem.class, id("foods")).buildAndRegister();
	public static Registry<FoodComponent> FOOD_COMPONENTS = FabricRegistryBuilder.createSimple(FoodComponent.class, id("custom_food_components")).buildAndRegister();
	public static Registry<CustomSoundGroup> BLOCK_SOUND_GROUPS = FabricRegistryBuilder.createSimple(CustomSoundGroup.class, id("block_sound_groups")).buildAndRegister();
	public static Registry<BlockSetType> BLOCK_SET_TYPES = FabricRegistryBuilder.createSimple(BlockSetType.class, id("block_set_types")).buildAndRegister();
	public static Registry<WoodType> WOOD_TYPES = FabricRegistryBuilder.createSimple(WoodType.class, id("wood_types")).buildAndRegister();
	public static Registry<MusicDisc> MUSIC_DISCS = FabricRegistryBuilder.createSimple(MusicDisc.class, id("music_discs")).buildAndRegister();
	public static Registry<KeyBinding> KEY_BINDINGS = FabricRegistryBuilder.createSimple(KeyBinding.class, id("key_bindings")).buildAndRegister();
	public static Registry<Particle> PARTICLES = FabricRegistryBuilder.createSimple(Particle.class, id("particles")).buildAndRegister();
	public static Registry<WeaponItem> WEAPONS = FabricRegistryBuilder.createSimple(WeaponItem.class, id("weapons")).buildAndRegister();
	public static Registry<RangedWeaponItem> RANGED_WEAPONS = FabricRegistryBuilder.createSimple(RangedWeaponItem.class, id("ranged_weapons")).buildAndRegister();
	public static Registry<ToolItem> TOOLS = FabricRegistryBuilder.createSimple(ToolItem.class, id("tools")).buildAndRegister();
	public static Registry<Block> BLOCKS = FabricRegistryBuilder.createSimple(Block.class, id("blocks")).buildAndRegister();
	public static Registry<FuelSource> FUEL_SOURCES = FabricRegistryBuilder.createSimple(FuelSource.class, id("fuel_sources")).buildAndRegister();
	public static Registry<Block> ORES = FabricRegistryBuilder.createSimple(Block.class, id("ores")).buildAndRegister();
	public static Registry<Potion> POTIONS = FabricRegistryBuilder.createSimple(Potion.class, id("potions")).buildAndRegister();
	public static Registry<Command.CommandNode> COMMANDS = FabricRegistryBuilder.createSimple(Command.CommandNode.class, id("commands")).buildAndRegister();
	public static Registry<StatusEffect> STATUS_EFFECTS = FabricRegistryBuilder.createSimple(StatusEffect.class, id("status_effects")).buildAndRegister();
	public static Registry<Enchantment> ENCHANTMENTS = FabricRegistryBuilder.createSimple(Enchantment.class, id("enchantments")).buildAndRegister();
	public static Registry<ItemGroup> ITEM_GROUPS = FabricRegistryBuilder.createSimple(ItemGroup.class, id("item_groups_registry")).buildAndRegister();
	public static Registry<CreativeTab> CREATIVE_TABS = FabricRegistryBuilder.createSimple(CreativeTab.class, id("creative_tabs")).buildAndRegister();
//	public static Registry<TabbedGroup> EXPANDED_ITEM_GROUPS = FabricRegistryBuilder.createSimple(TabbedGroup.class, id("expanded_item_groups_registry")).buildAndRegister();
	public static Registry<CondensedEntry> CONDENSED_ITEM_ENTRIES = FabricRegistryBuilder.createSimple(CondensedEntry.class, id("condensed_item_entries")).buildAndRegister();
	public static Registry<Entity> ENTITIES = FabricRegistryBuilder.createSimple(Entity.class, id("entities")).buildAndRegister();
	public static Registry<EntityModel> ENTITY_MODELS = FabricRegistryBuilder.createSimple(EntityModel.class, id("entity_models")).buildAndRegister();
	public static Registry<ArmorMaterial> ARMOR_MATERIALS = FabricRegistryBuilder.createSimple(ArmorMaterial.class, id("armor_materials")).buildAndRegister();
	public static Registry<ArmorModel> ARMOR_MODELS = FabricRegistryBuilder.createSimple(ArmorModel.class, id("armor_models")).buildAndRegister();
	public static Registry<ArmorItem> ARMORS = FabricRegistryBuilder.createSimple(ArmorItem.class, id("armors")).buildAndRegister();
	public static Registry<Elytra> ELYTRAS = FabricRegistryBuilder.createSimple(Elytra.class, id("elytras")).buildAndRegister();
//	public static Registry<ZoomableItem> ZOOMABLE_ITEMS = FabricRegistryBuilder.createSimple(ZoomableItem.class, id("zoomable_items")).buildAndRegister();
	public static Registry<CauldronType> CAULDRON_TYPES = FabricRegistryBuilder.createSimple(CauldronType.class, id("cauldron_types")).buildAndRegister();
	public static Registry<Painting> PAINTINGS = FabricRegistryBuilder.createSimple(Painting.class, id("paintings")).buildAndRegister();
	public static Registry<ShieldItem> SHIELDS = FabricRegistryBuilder.createSimple(ShieldItem.class, id("shields")).buildAndRegister();
	public static Registry<VillagerProfession> VILLAGER_PROFESSIONS = FabricRegistryBuilder.createSimple(VillagerProfession.class, id("villager_professions")).buildAndRegister();
	public static Registry<VillagerBiomeType> VILLAGER_BIOME_TYPES = FabricRegistryBuilder.createSimple(VillagerBiomeType.class, id("villager_biome_types")).buildAndRegister();
	public static Registry<Fluid> FLUIDS = FabricRegistryBuilder.createSimple(Fluid.class, id("fluids")).buildAndRegister();
	public static Registry<Emoji> EMOJIS = FabricRegistryBuilder.createSimple(Emoji.class, id("emojis")).buildAndRegister();
	public static Registry<GUI> GUIS = FabricRegistryBuilder.createSimple(GUI.class, id("guis")).buildAndRegister();
	public static Registry<HUD> HUDS = FabricRegistryBuilder.createSimple(HUD.class, id("huds")).buildAndRegister();

	//World Generation
	public static Registry<Tree> TREES = FabricRegistryBuilder.createSimple(Tree.class, id("trees")).buildAndRegister();
	public static Registry<Structure> STRUCTURES = FabricRegistryBuilder.createSimple(Structure.class, id("structures")).buildAndRegister();
	public static Registry<Biome> BIOMES = FabricRegistryBuilder.createSimple(Biome.class, id("biomes")).buildAndRegister();
}
